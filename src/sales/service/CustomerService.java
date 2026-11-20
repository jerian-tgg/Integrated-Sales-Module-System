package sales.service;

import java.util.List;
import sales.dao.CustomerDao;
import sales.model.Customer;

public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAll() {
        return customerDao.findAll();
    }

    public List<Customer> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customerDao.findAll();
        }
        return customerDao.search(keyword.trim());
    }

    public Customer saveCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer details are required.");
        }
        if (customer.getCustomerName() == null || customer.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        customer.setCustomerName(customer.getCustomerName().trim());
        customer.setContact(trimOrEmpty(customer.getContact()));
        customer.setAddress(trimOrEmpty(customer.getAddress()));

        if (customer.getId() <= 0) {
            customerDao.insert(customer);
        } else {
            customerDao.update(customer);
        }
        return customer;
    }

    public void deleteCustomer(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Please select a customer to delete.");
        }
        customerDao.delete(customerId);
    }

    private String trimOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
