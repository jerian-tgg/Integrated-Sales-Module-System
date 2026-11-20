package sales.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private int id;
    private String productCode;
    private String productName;
    private BigDecimal unitPrice;
    private int stockQty;

    public Product() {
        this.unitPrice = BigDecimal.ZERO;
    }

    public Product(int id, String productCode, String productName, BigDecimal unitPrice, int stockQty) {
        this.id = id;
        this.productCode = productCode;
        this.productName = productName;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.stockQty = stockQty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    @Override
    public String toString() {
        String code = productCode != null ? productCode : "";
        String name = productName != null ? productName : "";
        return code + " - " + name + " (Stock: " + stockQty + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Product other)) {
            return false;
        }
        if (this.id > 0 && other.id > 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.productCode, other.productCode);
    }

    @Override
    public int hashCode() {
        return id > 0 ? Integer.hashCode(id) : Objects.hashCode(productCode);
    }
}
