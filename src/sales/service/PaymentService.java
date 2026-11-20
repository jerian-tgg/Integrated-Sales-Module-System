package sales.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import sales.dao.EmployeeDao;
import sales.dao.PaymentDao;
import sales.model.Payment;

public class PaymentService {

    private static final Set<String> VALID_METHODS = Set.of("CASH", "BANK_TRANSFER", "CHECK", "CARD");
    private final PaymentDao paymentDao;
    private final EmployeeDao employeeDao;

    public PaymentService(PaymentDao paymentDao, EmployeeDao employeeDao) {
        this.paymentDao = paymentDao;
        this.employeeDao = employeeDao;
    }

    public List<Payment> getAll() {
        return paymentDao.findAll();
    }

    public List<Payment> getByInvoiceId(int invoiceId) {
        if (invoiceId <= 0) {
            return paymentDao.findAll();
        }
        return paymentDao.findByInvoiceId(invoiceId);
    }

    public Payment recordPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment details are required.");
        }
        if (payment.getInvoiceId() <= 0) {
            throw new IllegalArgumentException("Please select an unpaid or partially paid invoice.");
        }
        if (payment.getEmployeeId() <= 0 || employeeDao.findById(payment.getEmployeeId()) == null) {
            throw new IllegalArgumentException("Please select a valid cashier from the Employee Registry.");
        }
        if (payment.getAmountPaid() == null || payment.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        String method = payment.getPaymentMethod() != null ? payment.getPaymentMethod().trim().toUpperCase() : "";
        if (!VALID_METHODS.contains(method)) {
            throw new IllegalArgumentException("Payment method must be CASH, BANK_TRANSFER, CHECK, or CARD.");
        }

        payment.setAmountPaid(payment.getAmountPaid().setScale(2, RoundingMode.HALF_UP));
        payment.setPaymentMethod(method);
        payment.setReferenceNo(payment.getReferenceNo() != null ? payment.getReferenceNo().trim() : "");
        payment.setRemarks(payment.getRemarks() != null ? payment.getRemarks().trim() : "");

        return paymentDao.recordPayment(payment);
    }
}
