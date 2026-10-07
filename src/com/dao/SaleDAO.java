package com.dao;

import com.db.DBConnectionManager;
import com.model.Employe;
import com.model.Product;
import com.model.Sale;
import com.model.SaleDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {
    private Connection connection;
    private ProductDAO productDAO;

    public SaleDAO() {
        this.connection = DBConnectionManager.getInstance().getSharedConnection();
        this.productDAO = new ProductDAO();
    }

    public SaleDAO(Connection connection) {
        this.connection = connection;
        this.productDAO = new ProductDAO(connection);
    }

    public Sale insertSale(Sale sale) throws SQLException {
        String sqlSale = "INSERT INTO SALE (saleDate, totalPrice, givenByClient, changeToReturn, cashierId) " +
                "VALUES (?, ?, ?, ?, ?)";
        String sqlSaleDetail = "INSERT INTO SALE_DETAIL (quantitySold, unitSoldPrice, productId, saleId) " +
                "VALUES (?, ?, ?, ?)";
        String sqlTicket = "INSERT INTO TICKET (saleId) VALUES (?)";

        PreparedStatement psSale = null;
        PreparedStatement psSaleDetail = null;
        PreparedStatement psTicket = null;
        ResultSet rs = null;
        int generatedSaleId = 0;

        try {
            psSale = connection.prepareStatement(sqlSale, java.sql.Statement.RETURN_GENERATED_KEYS);
            psSale.setTimestamp(1, java.sql.Timestamp.valueOf(sale.getSaleDate()));
            psSale.setBigDecimal(2, sale.getTotalPrice());
            psSale.setBigDecimal(3, sale.getGivenByClient());
            psSale.setBigDecimal(4, sale.getChangeToReturn());
            psSale.setInt(5, sale.getCashier().getEmployeId());

            if (psSale.executeUpdate() == 0) {
                throw new SQLException("Echec de l'insertion de la vente.");
            }

            rs = psSale.getGeneratedKeys();
            if (rs.next()) {
                generatedSaleId = rs.getInt(1);
                sale.setSaleId(generatedSaleId);
            } else {
                throw new SQLException("Échec de la récupération de l'ID généré pour la vente.");
            }

            psSaleDetail = connection.prepareStatement(sqlSaleDetail);
            for (SaleDetail detail : sale.getSaleDetails()) {
                psSaleDetail.setInt(1, detail.getQuantitySold());
                psSaleDetail.setBigDecimal(2, detail.getUnitSoldPrice());
                psSaleDetail.setLong(3, detail.getProduct().getProductId());
                psSaleDetail.setInt(4, generatedSaleId);
                psSaleDetail.executeUpdate();
                psSaleDetail.clearParameters();
            }

            psTicket = connection.prepareStatement(sqlTicket);
            psTicket.setInt(1, generatedSaleId);
            psTicket.executeUpdate();

        } finally {
            DBConnectionManager.close(rs, psSale);
            DBConnectionManager.close(null, psTicket);
            DBConnectionManager.close(null, psSaleDetail);
        }
        return sale;
    }

    public Sale getSaleById(int saleId) {
        String sql = "SELECT * FROM SALE s " +
                "LEFT JOIN EMPLOYE e ON s.cashierId = e.employeId " +
                "WHERE s.saleId = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        Sale sale = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, saleId);
            rs = ps.executeQuery();

            if (rs.next()) {
                sale = extractSaleAndCashierFromResultSet(rs);
                List<SaleDetail> details = getSaleDetails(saleId);
                sale.setSaleDetails(details);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture de la vente : " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return sale;
    }

    public List<Sale> getAllSales() {
        String sql = "SELECT * FROM SALE s " +
                "LEFT JOIN EMPLOYE e ON s.cashierId = e.employeId";

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Sale> sales = new ArrayList<>();

        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                // Détails non chargés ici (1 requête par vente) : les lire avec getSaleDetails(saleId).
                sales.add(extractSaleAndCashierFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture de toutes les ventes : " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return sales;
    }

    private Sale extractSaleAndCashierFromResultSet(ResultSet rs) throws SQLException {
        Employe cashier = new Employe();
        cashier.setEmployeId(rs.getInt("cashierId"));
        cashier.setFirstName(rs.getString("firstName"));
        cashier.setFamilyName(rs.getString("familyName"));

        Sale sale = new Sale();
        sale.setSaleId(rs.getInt("saleId"));
        sale.setSaleDate(rs.getTimestamp("saleDate").toLocalDateTime());
        sale.setTotalPrice(rs.getBigDecimal("totalPrice"));
        sale.setGivenByClient(rs.getBigDecimal("givenByClient"));
        sale.setChangeToReturn(rs.getBigDecimal("changeToReturn"));

        sale.setCashier(cashier);

        return sale;
    }

    public List<SaleDetail> getSaleDetails(int saleId) {
        String sql = "SELECT * FROM SALE_DETAIL WHERE saleId = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SaleDetail> details = new ArrayList<>();

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, saleId);
            rs = ps.executeQuery();

            while (rs.next()) {
                SaleDetail detail = new SaleDetail();
                detail.setLigneId(rs.getInt("ligneId"));
                detail.setQuantitySold(rs.getInt("quantitySold"));
                detail.setUnitSoldPrice(rs.getBigDecimal("unitSoldPrice"));

                long productId = rs.getLong("productId");

                Product product = this.productDAO.getProductById(productId);
                detail.setProduct(product);

                details.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des détails de la vente (ID: " + saleId + "): " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }

        return details;
    }

    public boolean deleteSale(int saleId) {
        String sqlSaleDetail = "DELETE FROM SALE_DETAIL WHERE saleId = ?";
        String sqlTicket = "DELETE FROM TICKET WHERE saleId = ?";
        String sqlSale = "DELETE FROM SALE WHERE saleId = ?";
        PreparedStatement psSaleDetail = null;
        PreparedStatement psTicket = null;
        PreparedStatement psSale = null;
        boolean deleted = false;

        try {
            connection.setAutoCommit(false);

            psSaleDetail = connection.prepareStatement(sqlSaleDetail);
            psSaleDetail.setInt(1, saleId);
            psSaleDetail.executeUpdate();

            psTicket = connection.prepareStatement(sqlTicket);
            psTicket.setInt(1, saleId);
            psTicket.executeUpdate();

            psSale = connection.prepareStatement(sqlSale);
            psSale.setInt(1, saleId);

            if (psSale.executeUpdate() > 0) {
                connection.commit();
                deleted = true;
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression de la vente (ID: " + saleId + "): " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erreur lors du rollback : " + rollbackEx.getMessage());
            }
        } finally {
            DBConnectionManager.close(null, psSaleDetail);
            DBConnectionManager.close(null, psTicket);
            DBConnectionManager.close(null, psSale);
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la restauration de l'auto-commit: " + e.getMessage());
            }
        }
        return deleted;
    }

    public void deleteSalesByCashierId(int cashierId) {
        String sqlSaleDetails = "DELETE sd FROM SALE_DETAIL sd JOIN SALE s ON sd.saleId = s.saleId WHERE s.cashierId = ?";
        String sqlSales = "DELETE FROM SALE WHERE cashierId = ?";

        try (PreparedStatement psDetails = connection.prepareStatement(sqlSaleDetails);
             PreparedStatement psSales = connection.prepareStatement(sqlSales)) {

            connection.setAutoCommit(false);

            psDetails.setInt(1, cashierId);
            psDetails.executeUpdate();

            psSales.setInt(1, cashierId);
            psSales.executeUpdate();

            connection.commit();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des ventes pour le caissier ID " + cashierId + ": " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback: " + ex.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la restauration de l'auto-commit: " + e.getMessage());
            }
        }
    }
}
