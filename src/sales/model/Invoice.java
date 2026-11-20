package sales.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Invoice {

    private int id;
    private String invoiceNo;
    private int orderId;
    private String orderNo;
    private int customerId;
    private String customerName;
    private int employeeId;
    private String employeeName;
    private LocalDateTime invoiceDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal balance;
    private String status;
    private List<InvoiceLine> lines;

    public Invoice() {
        this.totalAmount = BigDecimal.ZERO;
        this.amountPaid = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
        this.lines = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid != null ? amountPaid : BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLine> lines) {
        this.lines = lines != null ? lines : new ArrayList<>();
    }

    @Override
    public String toString() {
        String no = invoiceNo != null ? invoiceNo : "";
        String cust = customerName != null ? customerName : "";
        BigDecimal bal = balance != null ? balance : BigDecimal.ZERO;
        return no + " - " + cust + " (Bal: " + bal.toPlainString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Invoice other)) {
            return false;
        }
        if (this.id > 0 && other.id > 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.invoiceNo, other.invoiceNo);
    }

    @Override
    public int hashCode() {
        return id > 0 ? Integer.hashCode(id) : Objects.hashCode(invoiceNo);
    }
}
