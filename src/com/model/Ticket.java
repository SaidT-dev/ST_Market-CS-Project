package com.model;

import java.util.Objects;

public class Ticket {
    private int ticketId;
    private Sale sale;

    public Ticket() {

    }

    public  Ticket(int ticketId, Sale sale) {
        this.ticketId = ticketId;
        this.sale = sale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return ticketId == ticket.ticketId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", sale=" + sale +
                '}';
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }
}
