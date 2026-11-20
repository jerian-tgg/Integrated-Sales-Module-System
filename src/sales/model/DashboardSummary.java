package sales.model;

import java.math.BigDecimal;

public class DashboardSummary {

    private int totalCustomers;
    private int totalProducts;
    private int openOrders;
    private int confirmedOrders;
    private int unpaidInvoices;
    private BigDecimal totalCollections;

    public DashboardSummary() {
        this.totalCollections = BigDecimal.ZERO;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getOpenOrders() {
        return openOrders;
    }

    public void setOpenOrders(int openOrders) {
        this.openOrders = openOrders;
    }

    public int getConfirmedOrders() {
        return confirmedOrders;
    }

    public void setConfirmedOrders(int confirmedOrders) {
        this.confirmedOrders = confirmedOrders;
    }

    public int getUnpaidInvoices() {
        return unpaidInvoices;
    }

    public void setUnpaidInvoices(int unpaidInvoices) {
        this.unpaidInvoices = unpaidInvoices;
    }

    public BigDecimal getTotalCollections() {
        return totalCollections;
    }

    public void setTotalCollections(BigDecimal totalCollections) {
        this.totalCollections = totalCollections != null ? totalCollections : BigDecimal.ZERO;
    }
}
