package sales.model;

import java.util.Objects;

public class Customer {

    private int id;
    private String customerName;
    private String contact;
    private String address;

    public Customer() {
    }

    public Customer(int id, String customerName, String contact, String address) {
        this.id = id;
        this.customerName = customerName;
        this.contact = contact;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return customerName != null ? customerName : "";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Customer other)) {
            return false;
        }
        if (this.id > 0 && other.id > 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.customerName, other.customerName);
    }

    @Override
    public int hashCode() {
        return id > 0 ? Integer.hashCode(id) : Objects.hashCode(customerName);
    }
}
