package service;

import dao.ReportDAO;

import java.util.List;

public class ReportService {
    private ReportDAO reportDAO = new ReportDAO();

    public Object[] getRevenueAndProfitByMonthData(int month, int year) {
        return reportDAO.getRevenueAndProfitByMonthData(month, year);
    }

    public List<Object[]> getRevenueAndProfitByYearData(int year) {
        return reportDAO.getRevenueAndProfitByYearData(year);
    }

    public List<Object[]> getRevenueByEmployeeData(int year) {
        return reportDAO.getRevenueByEmployeeData(year);
    }

    public List<Object[]> getTopSellingProductsData() {
        return reportDAO.getTopSellingProductsData();
    }

    public List<Object[]> getLowStockProductsData(int threshold) {
        return reportDAO.getLowStockProductsData(threshold);
    }

    public void displayRevenueAndProfitByMonth(int month, int year) {
        if (month < 1 || month > 12) {
            System.out.println("Lỗi: Tháng không hợp lệ (Phải từ 1 - 12)!");
            return;
        }
        reportDAO.displayRevenueAndProfitByMonth(month, year);
    }

    public void displayRevenueByEmployee(int year) {
        reportDAO.displayRevenueByEmployee(year);
    }

    public void displayTopSellingProducts() {
        reportDAO.displayTopSellingProducts();
    }

    public void displayLowStockProducts(int threshold) {
        reportDAO.displayLowStockProducts(threshold);
    }
}