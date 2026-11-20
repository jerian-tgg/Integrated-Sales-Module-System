package sales.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sales.dao.CustomerDao;
import sales.dao.EmployeeDao;
import sales.dao.ProductDao;
import sales.dao.SalesOrderDao;
import sales.model.Product;
import sales.model.SalesOrder;
import sales.model.SalesOrderLine;

public class SalesOrderService {

    private final SalesOrderDao salesOrderDao;
    private final CustomerDao customerDao;
    private final EmployeeDao employeeDao;
    private final ProductDao productDao;

    public SalesOrderService(SalesOrderDao salesOrderDao, CustomerDao customerDao,
            EmployeeDao employeeDao, ProductDao productDao) {
        this.salesOrderDao = salesOrderDao;
        this.customerDao = customerDao;
        this.employeeDao = employeeDao;
        this.productDao = productDao;
    }

    public List<SalesOrder> getAll() {
        return salesOrderDao.findAll();
    }

    public List<SalesOrder> getConfirmedOrders() {
        return salesOrderDao.findByStatus("CONFIRMED");
    }

    public SalesOrder getByIdWithLines(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Invalid sales order ID.");
        }
        return salesOrderDao.findByIdWithLines(orderId);
    }

    public SalesOrder createOrder(SalesOrder order) {
        prepareAndValidateOrder(order);
        return salesOrderDao.createOrder(order);
    }

    public void updateOrder(SalesOrder order) {
        if (order == null || order.getId() <= 0) {
            throw new IllegalArgumentException("Please select an existing OPEN sales order to update.");
        }
        prepareAndValidateOrder(order);
        salesOrderDao.updateOrder(order);
    }

    public void confirmOrder(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Please select a sales order to confirm.");
        }
        SalesOrder order = salesOrderDao.findByIdWithLines(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Sales order was not found.");
        }
        if (!"OPEN".equals(order.getStatus())) {
            throw new IllegalStateException("Only OPEN orders can be confirmed (current status: " + order.getStatus() + ").");
        }
        if (order.getLines().isEmpty()) {
            throw new IllegalStateException("Cannot confirm an order with no line items.");
        }
        for (SalesOrderLine line : order.getLines()) {
            Product product = productDao.findById(line.getProductId());
            if (product == null) {
                throw new IllegalStateException("Product ID " + line.getProductId() + " no longer exists.");
            }
            if (line.getQuantity() > product.getStockQty()) {
                throw new IllegalStateException("Insufficient stock to confirm " + product.getProductCode()
                        + " - " + product.getProductName() + " (available: " + product.getStockQty()
                        + ", ordered: " + line.getQuantity() + ").");
            }
        }
        salesOrderDao.confirmOrder(orderId);
    }

    public void cancelOrder(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Please select a sales order to cancel.");
        }
        salesOrderDao.cancelOrder(orderId);
    }

    public void deleteOrder(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Please select a sales order to delete.");
        }
        salesOrderDao.deleteOrder(orderId);
    }

    private void prepareAndValidateOrder(SalesOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Sales order data is required.");
        }
        if (order.getCustomerId() <= 0 || customerDao.findById(order.getCustomerId()) == null) {
            throw new IllegalArgumentException("Please select a valid customer from the Customer Registry.");
        }
        if (order.getEmployeeId() <= 0 || employeeDao.findById(order.getEmployeeId()) == null) {
            throw new IllegalArgumentException("Please select a valid sales representative from the Employee Registry.");
        }
        List<SalesOrderLine> rawLines = order.getLines();
        if (rawLines == null || rawLines.isEmpty()) {
            throw new IllegalArgumentException("Please add at least one product line item to the sales order.");
        }

        Map<Integer, SalesOrderLine> mergedByProduct = new LinkedHashMap<>();
        for (SalesOrderLine raw : rawLines) {
            if (raw.getProductId() <= 0) {
                throw new IllegalArgumentException("Line item has an invalid product selection.");
            }
            if (raw.getQuantity() <= 0) {
                throw new IllegalArgumentException("Line item quantity must be greater than zero.");
            }
            SalesOrderLine existing = mergedByProduct.get(raw.getProductId());
            if (existing == null) {
                SalesOrderLine copy = new SalesOrderLine();
                copy.setProductId(raw.getProductId());
                copy.setQuantity(raw.getQuantity());
                copy.setUnitPrice(raw.getUnitPrice());
                mergedByProduct.put(raw.getProductId(), copy);
            } else {
                existing.setQuantity(existing.getQuantity() + raw.getQuantity());
            }
        }

        List<SalesOrderLine> validatedLines = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (SalesOrderLine line : mergedByProduct.values()) {
            Product product = productDao.findById(line.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Selected product ID " + line.getProductId() + " no longer exists.");
            }
            if (line.getQuantity() > product.getStockQty()) {
                throw new IllegalArgumentException("Insufficient stock for " + product.getProductCode()
                        + " - " + product.getProductName() + " (available: " + product.getStockQty()
                        + ", requested: " + line.getQuantity() + ").");
            }
            BigDecimal price = (line.getUnitPrice() != null && line.getUnitPrice().compareTo(BigDecimal.ZERO) > 0)
                    ? line.getUnitPrice().setScale(2, RoundingMode.HALF_UP)
                    : product.getUnitPrice().setScale(2, RoundingMode.HALF_UP);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Unit price cannot be negative.");
            }
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(line.getQuantity())).setScale(2, RoundingMode.HALF_UP);

            line.setProductCode(product.getProductCode());
            line.setProductName(product.getProductName());
            line.setUnitPrice(price);
            line.setLineTotal(lineTotal);
            validatedLines.add(line);
            grandTotal = grandTotal.add(lineTotal);
        }

        order.setLines(validatedLines);
        order.setTotalAmount(grandTotal.setScale(2, RoundingMode.HALF_UP));
    }
}
