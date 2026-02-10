package com.model;

import java.math.BigDecimal;
import java.util.Objects;

public class SaleDetail {
    private int ligneId;
    private int quantitySold;
    private BigDecimal unitSoldPrice;
    private Sale sale;
    private Product product;

    public SaleDetail(){

    }

    public SaleDetail(int ligneId, int quantitySold, BigDecimal unitSoldPrice, Sale sale, Product product) {
        this.ligneId = ligneId;
        this.quantitySold = quantitySold;
        this.unitSoldPrice = unitSoldPrice;
        this.sale = sale;
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleDetail that = (SaleDetail) o;
        return ligneId == that.ligneId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ligneId);
    }

    @Override
    public String toString() {
        return "SaleDetail{" +
                "ligneId=" + ligneId +
                ", quantitySold=" + quantitySold +
                ", unitSoldPrice=" + unitSoldPrice +
                ", sale=" + sale +
                ", product=" + product +
                '}';
    }

    public int getLigneId() {
        return ligneId;
    }

    public void setLigneId(int ligneId) {
        this.ligneId = ligneId;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public BigDecimal getUnitSoldPrice() {
        return unitSoldPrice;
    }

    public void setUnitSoldPrice(BigDecimal unitSoldPrice) {
        this.unitSoldPrice = unitSoldPrice;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
