package service;

import dao.InvoiceDAO;
import model.InvoiceDetail;

import java.util.List;

public class InvoiceService {
    private InvoiceDAO invoiceDAO = new InvoiceDAO();

    public String createInvoice(int employeeId, int customerId, List<InvoiceDetail> details) {
        if (details == null || details.isEmpty()) {
            return "Chi tiết hóa đơn không được để trống!";
        }

        String errorMsg = invoiceDAO.createInvoice(employeeId, customerId, details);
        if (errorMsg == null) {
            System.out.println("\n==> TẠO HÓA ĐƠN THÀNH CÔNG! Đã tự động cập nhật trừ tồn kho.");
        } else {
            System.out.println("\n==> TẠO HÓA ĐƠN THẤT BẠI: " + errorMsg);
        }
        return errorMsg;
    }

    public boolean deleteInvoice(int invoiceId) {
        boolean success = invoiceDAO.deleteInvoice(invoiceId);
        if (success) {
            System.out.println("Xóa hóa đơn ID " + invoiceId + " thành công! Đã tự động hoàn trả tồn kho.");
        } else {
            System.out.println("Xóa hóa đơn thất bại! (Hóa đơn không tồn tại hoặc có lỗi hệ thống)");
        }
        return success;
    }

    public List<Object[]> getAllInvoicesData() {
        return invoiceDAO.getAllInvoicesData();
    }

    public List<Object[]> getInvoicesByCustomerName(String customerName) {
        return invoiceDAO.getInvoicesByCustomerName(customerName);
    }

    public void displayAllInvoices() {
        invoiceDAO.displayAllInvoices();
    }

    public void displayRevenueByMonth(int month, int year) {
        double revenue = invoiceDAO.getRevenueByMonthAndYear(month, year);
        System.out.printf("==> Tổng doanh thu tháng %d/%d: %,.2f VNĐ\n", month, year, revenue);
    }

    public void displayInvoicesByCustomerName(String customerName) {
        invoiceDAO.displayInvoicesByCustomerName(customerName);
    }

    public List<Object[]> getInvoiceDetailsByInvoiceId(int invoiceId) {
        return invoiceDAO.getInvoiceDetailsByInvoiceId(invoiceId);
    }
}