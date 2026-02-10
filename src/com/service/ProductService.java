package com.service;

import com.dao.ProductDAO;
import com.exception.ProductAlreadyExistsException;
import com.exception.ProductNotFoundException;
import com.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService(){
        this.productDAO = new ProductDAO();
    }


    public Product createProduct(Product product) throws ProductAlreadyExistsException {
        if(product == null){
            throw new IllegalArgumentException("Product cannot be null");
        }

        if(product.getProductName() == null || product.getProductName().trim().isEmpty()){
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        if(product.getUnitPrice() < 0){
            throw new IllegalArgumentException("Unit price cannot be negative");
        }

        if (product.getProductId() != 0) {
            Product existing = productDAO.getProductById(product.getProductId());
            if (existing != null) {
                throw new ProductAlreadyExistsException("Product with ID " + product.getProductId() + " already exists");
            }
        }

        try {
            return productDAO.insertProduct(product);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product findProductById(long productId){
        return productDAO.getProductById(productId);
    }

    public List<Product> findAllProducts(){
        return productDAO.getAllProduct();
    }

    public boolean updateProduct(Product product) throws ProductNotFoundException {
        if(product == null){
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        if(product.getUnitPrice() < 0){
            throw new IllegalArgumentException("Unit price cannot be negative");
        }

        Product existing = productDAO.getProductById(product.getProductId());
        if (existing == null) {
            throw new ProductNotFoundException("Product with ID " + product.getProductId() + " not found");
        }

        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(long productId) throws ProductNotFoundException{
        Product existing = productDAO.getProductById(productId);
        if (existing == null) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
        return productDAO.deleteProduct(productId);
    }

    public List<Product> findProductsWithLowStock(){
        return productDAO.getProductsWithLowStock();
    }

    public void restockProduct(long productId, int quantityToAdd) throws ProductNotFoundException {
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
        productDAO.updateStock(productId, quantityToAdd);
    }
}
