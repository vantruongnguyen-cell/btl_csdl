package dao;

import config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    // 1. Dữ liệu Doanh thu & Lợi nhuận Tháng/Năm
    public Object[] getRevenueAndProfitByMonthData(int month, int year) {
        String sqlRevenue = "SELECT COALESCE(SUM(total_amount), 0) AS total_revenue " +
                "FROM invoices WHERE EXTRACT(MONTH FROM created_at) = ? AND EXTRACT(YEAR FROM created_at) = ?";

        String sqlImportCost = "SELECT COALESCE(SUM(total_amount), 0) AS total_cost " +
                "FROM import_orders WHERE EXTRACT(MONTH FROM created_at) = ? AND EXTRACT(YEAR FROM created_at) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmtRev = conn.prepareStatement(sqlRevenue);
             PreparedStatement pstmtCost = conn.prepareStatement(sqlImportCost)) {

            pstmtRev.setInt(1, month);
            pstmtRev.setInt(2, year);

            pstmtCost.setInt(1, month);
            pstmtCost.setInt(2, year);

            double revenue = 0;
            double cost = 0;

            try (ResultSet rsRev = pstmtRev.executeQuery()) {
                if (rsRev.next()) revenue = rsRev.getDouble("total_revenue");
            }

            try (ResultSet rsCost = pstmtCost.executeQuery()) {
                if (rsCost.next()) cost = rsCost.getDouble("total_cost");
            }

            double profit = revenue - cost;

            return new Object[]{
                    String.format("%02d/%d", month, year),
                    String.format("%,.0f VNĐ", revenue),
                    String.format("%,.0f VNĐ", cost),
                    String.format("%,.0f VNĐ", profit)
            };

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ================= BỔ SUNG: Dữ liệu Doanh thu & Lợi nhuận 12 Tháng Theo Năm =================
    public List<Object[]> getRevenueAndProfitByYearData(int year) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT m.month, " +
                "COALESCE(rev.total_revenue, 0) AS total_revenue, " +
                "COALESCE(cost.total_cost, 0) AS total_cost " +
                "FROM generate_series(1, 12) AS m(month) " +
                "LEFT JOIN (" +
                "    SELECT EXTRACT(MONTH FROM created_at) AS month, SUM(total_amount) AS total_revenue " +
                "    FROM invoices WHERE EXTRACT(YEAR FROM created_at) = ? GROUP BY EXTRACT(MONTH FROM created_at)" +
                ") rev ON m.month = rev.month " +
                "LEFT JOIN (" +
                "    SELECT EXTRACT(MONTH FROM created_at) AS month, SUM(total_amount) AS total_cost " +
                "    FROM import_orders WHERE EXTRACT(YEAR FROM created_at) = ? GROUP BY EXTRACT(MONTH FROM created_at)" +
                ") cost ON m.month = cost.month " +
                "ORDER BY m.month ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            pstmt.setInt(2, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("month");
                    double revenue = rs.getDouble("total_revenue");
                    double cost = rs.getDouble("total_cost");
                    double profit = revenue - cost;

                    list.add(new Object[]{
                            "Tháng " + String.format("%02d/%d", month, year),
                            String.format("%,.0f VNĐ", revenue),
                            String.format("%,.0f VNĐ", cost),
                            String.format("%,.0f VNĐ", profit)
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Dữ liệu Doanh thu theo Nhân viên
    public List<Object[]> getRevenueByEmployeeData(int year) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT e.employee_id, e.employee_name, COUNT(i.invoice_id) AS total_invoices, COALESCE(SUM(i.total_amount), 0) AS total_sales " +
                "FROM employees e " +
                "LEFT JOIN invoices i ON e.employee_id = i.employee_id AND EXTRACT(YEAR FROM i.created_at) = ? " +
                "GROUP BY e.employee_id, e.employee_name " +
                "ORDER BY total_sales DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getInt("total_invoices"),
                            String.format("%,.0f VNĐ", rs.getDouble("total_sales"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Dữ liệu Top 5 sản phẩm bán chạy nhất
    public List<Object[]> getTopSellingProductsData() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, SUM(id.quantity) AS total_sold, SUM(id.quantity * id.unit_price) AS total_revenue " +
                "FROM invoice_details id " +
                "JOIN products p ON id.product_id = p.product_id " +
                "GROUP BY p.product_id, p.product_name " +
                "ORDER BY total_sold DESC LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rank = 1;
            while (rs.next()) {
                list.add(new Object[]{
                        "TOP " + (rank++),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("total_sold"),
                        String.format("%,.0f VNĐ", rs.getDouble("total_revenue"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 4. Dữ liệu Cảnh báo sản phẩm tồn kho dưới ngưỡng
    public List<Object[]> getLowStockProductsData(int threshold) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT product_id, product_name, brand, stock, price FROM products WHERE stock <= ? ORDER BY stock ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, threshold);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("brand"),
                            rs.getInt("stock"),
                            String.format("%,.0f VNĐ", rs.getDouble("price"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void displayRevenueAndProfitByMonth(int month, int year) {
        String sqlRevenue = "SELECT COALESCE(SUM(total_amount), 0) AS total_revenue " +
                "FROM invoices WHERE EXTRACT(MONTH FROM created_at) = ? AND EXTRACT(YEAR FROM created_at) = ?";

        String sqlImportCost = "SELECT COALESCE(SUM(total_amount), 0) AS total_cost " +
                "FROM import_orders WHERE EXTRACT(MONTH FROM created_at) = ? AND EXTRACT(YEAR FROM created_at) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmtRev = conn.prepareStatement(sqlRevenue);
             PreparedStatement pstmtCost = conn.prepareStatement(sqlImportCost)) {

            pstmtRev.setInt(1, month);
            pstmtRev.setInt(2, year);

            pstmtCost.setInt(1, month);
            pstmtCost.setInt(2, year);

            double revenue = 0;
            double cost = 0;

            try (ResultSet rsRev = pstmtRev.executeQuery()) {
                if (rsRev.next()) revenue = rsRev.getDouble("total_revenue");
            }

            try (ResultSet rsCost = pstmtCost.executeQuery()) {
                if (rsCost.next()) cost = rsCost.getDouble("total_cost");
            }

            double profit = revenue - cost;

            System.out.println("\n================ BÁO CÁO TÀI CHÍNH THÁNG " + month + "/" + year + " ================");
            System.out.printf("  1. Tổng Doanh Thu Bán Hàng : %,.2f VNĐ\n", revenue);
            System.out.printf("  2. Tổng Chi Phí Nhập Hàng  : %,.2f VNĐ\n", cost);
            System.out.println("  -------------------------------------------------------------");
            System.out.printf("  ==> LỢI NHUẬN RÒNG (Lãi/Lỗ): %,.2f VNĐ\n", profit);
            System.out.println("=================================================================");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayRevenueByEmployee(int year) {
        String sql = "SELECT e.employee_id, e.employee_name, COUNT(i.invoice_id) AS total_invoices, COALESCE(SUM(i.total_amount), 0) AS total_sales " +
                "FROM employees e " +
                "LEFT JOIN invoices i ON e.employee_id = i.employee_id AND EXTRACT(YEAR FROM i.created_at) = ? " +
                "GROUP BY e.employee_id, e.employee_name " +
                "ORDER BY total_sales DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n================ DOANH THU THEO NHÂN VIÊN (NĂM " + year + ") ================");
                System.out.printf("%-8s | %-25s | %-15s | %-18s\n", "Mã NV", "Tên Nhân Viên", "Số Hóa Đơn", "Tổng Doanh Số (VND)");
                System.out.println("-----------------------------------------------------------------------------");

                while (rs.next()) {
                    System.out.printf("%-8d | %-25s | %-15d | %-18.2f\n",
                            rs.getInt("employee_id"),
                            rs.getString("employee_name"),
                            rs.getInt("total_invoices"),
                            rs.getDouble("total_sales"));
                }
                System.out.println("=============================================================================");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayTopSellingProducts() {
        String sql = "SELECT p.product_id, p.product_name, SUM(id.quantity) AS total_sold, SUM(id.quantity * id.unit_price) AS total_revenue " +
                "FROM invoice_details id " +
                "JOIN products p ON id.product_id = p.product_id " +
                "GROUP BY p.product_id, p.product_name " +
                "ORDER BY total_sold DESC LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n==================== TOP 5 SẢN PHẨM BÁN CHẠY NHẤT ====================");
            System.out.printf("%-8s | %-30s | %-12s | %-18s\n", "Mã SP", "Tên Sản Phẩm", "Số Lượng Bán", "Doanh Thu (VND)");
            System.out.println("-----------------------------------------------------------------------");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.printf("%-8d | %-30s | %-12d | %-18.2f\n",
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("total_sold"),
                        rs.getDouble("total_revenue"));
            }

            if (!hasData) {
                System.out.println("Chưa có dữ liệu bán hàng.");
            }
            System.out.println("=======================================================================");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayLowStockProducts(int threshold) {
        String sql = "SELECT product_id, product_name, stock, price FROM products WHERE stock <= ? ORDER BY stock ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, threshold);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n=================== CẢNH BÁO TỒN KHO DƯỚI NGƯỠNG (" + threshold + ") ===================");
                System.out.printf("%-8s | %-30s | %-12s | %-15s\n", "Mã SP", "Tên Sản Phẩm", "Tồn Kho Hiện Tại", "Giá Bán (VND)");
                System.out.println("----------------------------------------------------------------------------");

                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    System.out.printf("%-8d | %-30s | %-12d | %-15.2f\n",
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("stock"),
                            rs.getDouble("price"));
                }

                if (!hasData) {
                    System.out.println("Tất cả sản phẩm đều có số lượng tồn kho an toàn!");
                }
                System.out.println("============================================================================");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}