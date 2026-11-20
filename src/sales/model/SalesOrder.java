package sales.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SalesOrder {

    private int id;
    private String orderNo;
    private int customerId;
    private String customerName;
    private int employeeId;
    private String employeeName;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private List<SalesOrderLine> lines;

    public SalesOrder() {
        this.totalAmount = BigDecimal.ZERO;
        this.lines = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    public List<SalesOrderLine> getLines() {
        return lines;
    }

    public void setLines(List<SalesOrderLine> lines) {
        this.lines = lines != null ? lines : new ArrayList<>();
    }

    @Override
    public String toString() {
        String no = orderNo != null ? orderNo : "";
        String cust = customerName != null ? customerName : "";
        BigDecimal amt = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        return no + " - " + cust + " (" + amt.toPlainString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SalesOrder other)) {
            return false;
        }
        if (this.id > 0 && other.id > 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.orderNo, other.orderNo);
    }

    @Override
    public int hashCode() {
        return id > 0 ? Integer.hashCode(id) : Objects.hashCode(orderNo);
    }
}
