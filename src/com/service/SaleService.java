package com.service;

import com.dao.ProductDAO;
import com.dao.SaleDAO;
import com.db.DBConnectionManager;
import com.exception.InsufficientStockException;
import com.model.Product;
import com.model.Sale;
import com.model.SaleDetail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SaleService {

    private final ProductDAO productDAO;
    private final SaleDAO saleDAO;

    public SaleService(){
        this.productDAO = new ProductDAO();
        this.saleDAO = new SaleDAO();
    }

    public Sale createSale(Sale sale) throws InsufficientStockException {
        if(sale == null) throw new IllegalArgumentException("Vente null");

        Connection connection = null;

        try {
            connection = DBConnectionManager.getInstance().getConnection();
            connection.setAutoCommit(false);

            ProductDAO transacProductDAO = new ProductDAO(connection);
            SaleDAO transacSaleDAO = new SaleDAO(connection);

            for (SaleDetail saleDetail : sale.getSaleDetails()) {
                Product product = transacProductDAO.getProductById(saleDetail.getProduct().getProductId());

                if (product.getStock().getCurrentQuantity() < saleDetail.getQuantitySold()) {
                    throw new InsufficientStockException("Stock insuffisant : " + product.getProductName());
                }

                transacProductDAO.updateStock(product.getProductId(), -saleDetail.getQuantitySold());
            }

            Sale insertedSale = transacSaleDAO.insertSale(sale);

            connection.commit();
            return insertedSale;

        } catch (InsufficientStockException e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw e;
        } catch (SQLException e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Erreur Transaction : " + e.getMessage(), e);
        } finally {
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<Sale> findAllSales() {
        return saleDAO.getAllSales();
    }

    public List<SaleDetail> getSaleDetails(int saleId) {
        return saleDAO.getSaleDetails(saleId);
    }
}
