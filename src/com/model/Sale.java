package com.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Sale {
    private int saleId;
    private LocalDateTime saleDate;
    private BigDecimal totalPrice;
    private BigDecimal givenByClient;
    private BigDecimal changeToReturn;
    private Employe cashier;
    private List<SaleDetail> saleDetails;
    private Ticket ticket;

    public Sale() {
    }

    public Sale(int saleId, LocalDateTime saleDate, BigDecimal totalPrice, BigDecimal givenByClient,
                BigDecimal changeToReturn, Employe cashier, List<SaleDetail> saleDetails) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.totalPrice = totalPrice;
        this.givenByClient = givenByClient;
        this.changeToReturn = changeToReturn;
        this.cashier = cashier;
        this.saleDetails = saleDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sale sale = (Sale) o;
        return saleId == sale.saleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleId);
    }

    @Override
    public String toString() {
        return "Sale{" +
                "saleId=" + saleId +
                ", saleDate=" + saleDate +
                ", totalPrice=" + totalPrice +
                ", givenByClient=" + givenByClient +
                ", changeToReturn=" + changeToReturn +
                '}';
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getGivenByClient() {
        return givenByClient;
    }

    public void setGivenByClient(BigDecimal givenByClient) {
        this.givenByClient = givenByClient;
    }

    public BigDecimal getChangeToReturn() {
        return changeToReturn;
    }

    public void setChangeToReturn(BigDecimal changeToReturn) {
        this.changeToReturn = changeToReturn;
    }

    public Employe getCashier() {
        return cashier;
    }

    public void setCashier(Employe cashier) {
        this.cashier = cashier;
    }

    public List<SaleDetail> getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(List<SaleDetail> saleDetails) {
        this.saleDetails = saleDetails;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
