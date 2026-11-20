package sales.service;

import java.time.LocalDate;
import java.util.List;
import sales.dao.EmployeeDao;
import sales.dao.InvoiceDao;
import sales.model.Invoice;

public class InvoiceService {

    private final InvoiceDao invoiceDao;
    private final EmployeeDao employeeDao;

    public InvoiceService(InvoiceDao invoiceDao, EmployeeDao employeeDao) {
        this.invoiceDao = invoiceDao;
        this.employeeDao = employeeDao;
    }

    public List<Invoice> getAll() {
        return invoiceDao.findAll();
    }

    public List<Invoice> getUnpaidOrPartialInvoices() {
        return invoiceDao.findUnpaidAndPartial();
    }

    public Invoice getByIdWithLines(int invoiceId) {
        if (invoiceId <= 0) {
            throw new IllegalArgumentException("Invalid invoice ID.");
        }
        return invoiceDao.findByIdWithLines(invoiceId);
    }

    public Invoice generateInvoice(int orderId, int employeeId, LocalDate dueDate) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Please select a CONFIRMED sales order to invoice.");
        }
        if (employeeId <= 0 || employeeDao.findById(employeeId) == null) {
            throw new IllegalArgumentException("Valid logged-in employee is required to generate an invoice.");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date is required.");
        }
        return invoiceDao.generateInvoiceFromOrder(orderId, employeeId, dueDate);
    }
}
