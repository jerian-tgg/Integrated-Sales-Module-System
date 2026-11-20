package sales.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import sales.dao.ProductDao;
import sales.model.Product;

public class ProductService {

    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> getAll() {
        return productDao.findAll();
    }

    public List<Product> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productDao.findAll();
        }
        return productDao.search(keyword.trim());
    }

    public Product saveProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product details are required.");
        }
        String code = requireText(product.getProductCode(), "Product code").toUpperCase();
        String name = requireText(product.getProductName(), "Product name");

        if (product.getUnitPrice() == null || product.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price must be zero or greater.");
        }
        if (product.getStockQty() < 0) {
            throw new IllegalArgumentException("Stock quantity must be zero or greater.");
        }

        int excludeId = product.getId() > 0 ? product.getId() : 0;
        if (productDao.existsByProductCode(code, excludeId)) {
            throw new IllegalArgumentException("Product code '" + code + "' already exists.");
        }

        product.setProductCode(code);
        product.setProductName(name);
        product.setUnitPrice(product.getUnitPrice().setScale(2, RoundingMode.HALF_UP));

        if (product.getId() <= 0) {
            productDao.insert(product);
        } else {
            productDao.update(product);
        }
        return product;
    }

    public void deleteProduct(int productId) {
        if (productId <= 0) {
            throw new IllegalArgumentException("Please select a product to delete.");
        }
        productDao.delete(productId);
    }

    private String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }
}
