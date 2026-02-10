package com.dao;

import com.db.DBConnectionManager;
import com.model.Product;
import com.model.Stock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private Connection connection;
    
    public ProductDAO() {
        try {
            this.connection = DBConnectionManager.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur DAO : Impossible d'obtenir la connexion à la base de données.", e);
        }
    }

    public ProductDAO(Connection connection) {
        this.connection = connection;

    }

    public Product insertProduct(Product product) throws SQLException {
        String sqlProduct = "INSERT INTO PRODUCT (productId, productName, unitPrice) VALUES (?, ?, ?)";
        String sqlStock = "INSERT INTO STOCK (productId, currentQuantity, minimalQuantity) VALUES (?, ?, ?)";

        PreparedStatement psProduct = null;
        PreparedStatement psStock = null;

        try {
            connection.setAutoCommit(false);

            psProduct = connection.prepareStatement(sqlProduct);
            psProduct.setLong(1, product.getProductId());
            psProduct.setString(2, product.getProductName());
            psProduct.setDouble(3, product.getUnitPrice());

            if(psProduct.executeUpdate() == 0) {
                throw new SQLException("Echec de l'insertion du produit.");
            }

            Stock stock = product.getStock();

            psStock = connection.prepareStatement(sqlStock);
            psStock.setLong(1, product.getProductId());
            psStock.setInt(2, stock.getCurrentQuantity());
            psStock.setInt(3, stock.getMinimalQuantity());

            if(psStock.executeUpdate() == 0) {
                throw new SQLException("Echec de l'insertion du stock.");
            }
            connection.commit();

        } catch (SQLException e){
            if (e.getSQLState().equals("23000")) { // SQL state for integrity constraint violation
                throw new SQLIntegrityConstraintViolationException(e);
            }
            System.err.println("Erreur de transaction. Annulation... " + e.getMessage());
            try{
                if(connection != null) connection.rollback();
            } catch (SQLException e1) {
                System.err.println("Erreur lors du rollback : " + e1.getMessage());
            }
        } finally {
            DBConnectionManager.close(null, psProduct);
            DBConnectionManager.close(null, psStock);
            try{
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return product;
    }

    public Product getProductById(long id) {
        String sql = "SELECT * FROM PRODUCT p " +
                "JOIN STOCK s ON p.productId = s.productId " +
                "WHERE p.productId = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        Product product = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                product = extractProductFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture du produit : " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return product;
    }

    public List<Product> getAllProduct() {
        String sql = "SELECT * FROM PRODUCT p " +
                "JOIN STOCK s ON p.productId = s.productId";

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> Products = new ArrayList<>();

        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Products.add(extractProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture de tous les produits: " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return Products;
    }

    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Stock stock = new Stock();

        stock.setProductId(rs.getLong("productId"));
        stock.setCurrentQuantity(rs.getInt("currentQuantity"));
        stock.setMinimalQuantity(rs.getInt("minimalQuantity"));

        Product product = new Product();
        product.setProductId(rs.getLong("productId"));
        product.setProductName(rs.getString("productName"));
        product.setUnitPrice(rs.getDouble("unitPrice"));

        product.setStock(stock);

        return product;
    }

    public boolean updateProduct(Product product) {
        // REQUÊTES pour les DEUX tables
        String sqlProduct = "UPDATE PRODUCT SET productName = ?, unitPrice = ? WHERE productId = ?";
        String sqlStock = "UPDATE STOCK SET currentQuantity = ?, minimalQuantity = ? WHERE productId = ?";

        PreparedStatement psProduct = null;
        PreparedStatement psStock = null;
        boolean updated = false;

        try {
            // Démarrer la transaction
            connection.setAutoCommit(false);

            // 1. Mettre à jour PRODUCT
            psProduct = connection.prepareStatement(sqlProduct);
            psProduct.setString(1, product.getProductName());
            psProduct.setDouble(2, product.getUnitPrice());
            psProduct.setLong(3, product.getProductId());
            psProduct.executeUpdate();

            // 2. Mettre à jour STOCK
            Stock stock = product.getStock();
            psStock = connection.prepareStatement(sqlStock);
            psStock.setInt(1, stock.getCurrentQuantity());
            psStock.setInt(2, stock.getMinimalQuantity());
            psStock.setLong(3, product.getProductId()); // ID du produit pour le WHERE
            psStock.executeUpdate();

            // Valider la transaction
            connection.commit();
            updated = true;

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour du produit (ID: " + product.getProductId() + "): " + e.getMessage());
            try {
                if (connection != null) connection.rollback(); // Annuler
            } catch (SQLException rollbackEx) {
                System.err.println("Erreur lors du rollback : " + rollbackEx.getMessage());
            }
        } finally {
            DBConnectionManager.close(null, psProduct);
            DBConnectionManager.close(null, psStock);
            try {
                connection.setAutoCommit(true); // Réactiver l'auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return updated;
    }

    public void updateStock(long productId, int quantityToAdd) {
        String sql = "UPDATE STOCK SET currentQuantity = currentQuantity + ? WHERE productId = ?";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, quantityToAdd);
            ps.setLong(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour du stock pour le produit (ID: " + productId + "): " + e.getMessage());
        } finally {
            DBConnectionManager.close(null, ps);
        }
    }

    public boolean deleteProduct(long id) {
        // REQUÊTES pour les TROIS tables
        String sqlSaleDetail = "DELETE FROM SALE_DETAIL WHERE productId = ?";
        String sqlStock = "DELETE FROM STOCK WHERE productId = ?";
        String sqlProduct = "DELETE FROM PRODUCT WHERE productId = ?";

        PreparedStatement psSaleDetail = null;
        PreparedStatement psStock = null;
        PreparedStatement psProduct = null;
        boolean deleted = false;

        try {
            // Démarrer la transaction
            connection.setAutoCommit(false);

            // 1. Supprimer de SALE_DETAIL (autre table enfant)
            psSaleDetail = connection.prepareStatement(sqlSaleDetail);
            psSaleDetail.setLong(1, id);
            psSaleDetail.executeUpdate(); // On ne vérifie pas le nombre de lignes, car un produit peut ne jamais avoir été vendu

            // 2. Supprimer de STOCK (table enfant)
            psStock = connection.prepareStatement(sqlStock);
            psStock.setLong(1, id);
            psStock.executeUpdate();

            // 3. Supprimer de PRODUCT (table parent)
            psProduct = connection.prepareStatement(sqlProduct);
            psProduct.setLong(1, id);
            int productRows = psProduct.executeUpdate();

            if (productRows > 0) { // On vérifie juste si le produit a été supprimé
                // Valider la transaction
                connection.commit();
                deleted = true;
            } else {
                // Si la suppression du produit a échoué
                throw new SQLException("Échec de la suppression (Produit affecté: " + productRows + ")");
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression du produit (ID: " + id + "): " + e.getMessage());
            try {
                if (connection != null) connection.rollback(); // Annuler
            } catch (SQLException rollbackEx) {
                System.err.println("Erreur lors du rollback : " + rollbackEx.getMessage());
            }
        } finally {
            DBConnectionManager.close(null, psSaleDetail);
            DBConnectionManager.close(null, psStock);
            DBConnectionManager.close(null, psProduct);
            try {
                connection.setAutoCommit(true); // Réactiver l'auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deleted;
    }

    public List<Product> getProductsWithLowStock() {
        String sql = "SELECT * FROM PRODUCT p " +
                "JOIN STOCK s ON p.productId = s.productId " +
                "WHERE s.currentQuantity <= s.minimalQuantity";

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Product> Products = new ArrayList<>();

        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Products.add(extractProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture de tous les produits: " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return Products;
    }


}
