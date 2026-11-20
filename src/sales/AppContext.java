package sales;

import com.zaxxer.hikari.HikariDataSource;
import sales.dao.CustomerDao;
import sales.dao.EmployeeDao;
import sales.dao.InvoiceDao;
import sales.dao.PaymentDao;
import sales.dao.ProductDao;
import sales.dao.ReportDao;
import sales.dao.SalesOrderDao;
import sales.model.Employee;
import sales.service.CustomerService;
import sales.service.EmployeeService;
import sales.service.InvoiceService;
import sales.service.PaymentService;
import sales.service.ProductService;
import sales.service.ReportService;
import sales.service.SalesOrderService;

/**
 * Central application dependency container exposing services and active session state.
 */
public class AppContext {

    private final HikariDataSource dataSource;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final SalesOrderService salesOrderService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final ReportService reportService;

    private Employee currentEmployee;

    public AppContext(HikariDataSource dataSource) {
        this.dataSource = dataSource;

        EmployeeDao employeeDao = new EmployeeDao(dataSource);
        CustomerDao customerDao = new CustomerDao(dataSource);
        ProductDao productDao = new ProductDao(dataSource);
        SalesOrderDao salesOrderDao = new SalesOrderDao(dataSource);
        InvoiceDao invoiceDao = new InvoiceDao(dataSource, productDao, salesOrderDao);
        PaymentDao paymentDao = new PaymentDao(dataSource);
        ReportDao reportDao = new ReportDao(dataSource);

        this.employeeService = new EmployeeService(employeeDao);
        this.customerService = new CustomerService(customerDao);
        this.productService = new ProductService(productDao);
        this.salesOrderService = new SalesOrderService(salesOrderDao, customerDao, employeeDao, productDao);
        this.invoiceService = new InvoiceService(invoiceDao, employeeDao);
        this.paymentService = new PaymentService(paymentDao, employeeDao);
        this.reportService = new ReportService(reportDao);
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public SalesOrderService getSalesOrderService() {
        return salesOrderService;
    }

    public InvoiceService getInvoiceService() {
        return invoiceService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    public void clearSession() {
        this.currentEmployee = null;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
