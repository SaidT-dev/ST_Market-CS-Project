package com.model;

import java.util.Objects;

public class Stock{
    private long productId;
    private int currentQuantity;
    private int minimalQuantity;

    public Stock(){
    }

    public Stock(long productId, int currentQuantity, int minimalQuantity) {
        this.productId = productId;
        this.currentQuantity = currentQuantity;
        this.minimalQuantity = minimalQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return productId == stock.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stockId=" + productId +
                ", currentQuantity=" + currentQuantity +
                ", minimalQuantity=" + minimalQuantity +
                '}';
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public int getMinimalQuantity() {
        return minimalQuantity;
    }

    public void setMinimalQuantity(int minimalQuantity) {
        this.minimalQuantity = minimalQuantity;
    }
}
