package com.model;

import java.util.Objects;

public class Product {
    private long productId;
    private String productName;
    private double unitPrice;
    private Stock stock;

    public Product() {
        this.stock = new Stock();
    }

    public Product(long productId, String productName, double unitPrice, Stock stock) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.stock = stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return this.productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + this.productId +
                ", productName='" + this.productName + '\'' +
                ", unitPrice='" + this.unitPrice + '\'' +
                ", stock=" + this.stock +
                '}';
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
