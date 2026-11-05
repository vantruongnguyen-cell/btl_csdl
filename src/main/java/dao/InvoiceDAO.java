package dao;

import config.DatabaseConnection;
import model.InvoiceDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    // Trả về String: null nếu THÀNH CÔNG, hoặc câu thông báo lỗi CỤ THỂ nếu THẤT BẠI
    public String createInvoice(int employeeId, int customerId, List<InvoiceDetail> details) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra sự tồn tại của Mã Nhân Viên
            String sqlCheckEmp = "SELECT employee_id FROM employees WHERE employee_id = ?";
            try (PreparedStatement pstmtEmp = conn.prepareStatement(sqlCheckEmp)) {
                pstmtEmp.setInt(1, employeeId);
                try (ResultSet rsEmp = pstmtEmp.executeQuery()) {
                    if (!rsEmp.next()) {
                        conn.rollback();
                        return "Mã nhân viên #" + employeeId + " không tồn tại trong hệ thống!";
                    }
                }
            }

            // 2. Kiểm tra sự tồn tại của Mã Khách Hàng
            String sqlCheckCus = "SELECT customer_id FROM customers WHERE customer_id = ?";
            try (PreparedStatement pstmtCus = conn.prepareStatement(sqlCheckCus)) {
                pstmtCus.setInt(1, customerId);
                try (ResultSet rsCus = pstmtCus.executeQuery()) {
                    if (!rsCus.next()) {
                        conn.rollback();
                        return "Mã khách hàng #" + customerId + " không tồn tại trong hệ thống!";
                    }
                }
            }

            // 3. Kiểm tra từng sản phẩm: Tồn tại & Tồn kho
            String sqlCheckProduct = "SELECT product_name, stock FROM products WHERE product_id = ?";
            for (InvoiceDetail item : details) {
                try (PreparedStatement pstmtProd = conn.prepareStatement(sqlCheckProduct)) {
                    pstmtProd.setInt(1, item.getProductID());
                    try (ResultSet rsProd = pstmtProd.executeQuery()) {
                        if (!rsProd.next()) {
                            conn.rollback();
                            return "Sản phẩm có Mã #" + item.getProductID() + " không tồn tại!";
                        } else {
                            String pName = rsProd.getString("product_name");
                            int stock = rsProd.getInt("stock");
                            if (stock < item.getQuantity()) {
                                conn.rollback();
                                return "Sản phẩm '" + pName + "' (Mã #" + item.getProductID() + ") không đủ tồn kho! (Còn: " + stock + ", Yêu cầu: " + item.getQuantity() + ")";
                            }
                        }
                    }
                }
            }

            // 4. Tính tổng tiền
            double totalAmount = 0;
            for (InvoiceDetail item : details) {
                totalAmount += item.getQuantity() * item.getUnitPrice();
            }

            // 5. Thêm Hóa đơn vào CSDL
            String sqlInvoice = "INSERT INTO invoices (employee_id, customer_id, total_amount) VALUES (?, ?, ?) RETURNING invoice_id";
            int invoiceId = 0;
            try (PreparedStatement pstmtInv = conn.prepareStatement(sqlInvoice)) {
                pstmtInv.setInt(1, employeeId);
                pstmtInv.setInt(2, customerId);
                pstmtInv.setDouble(3, totalAmount);
                try (ResultSet rsInv = pstmtInv.executeQuery()) {
                    if (rsInv.next()) {
                        invoiceId = rsInv.getInt(1);
                    }
                }
            }

            if (invoiceId == 0) {
                conn.rollback();
                return "Không thể khởi tạo mã hóa đơn!";
            }

            // 6. Trừ tồn kho & Thêm chi tiết hóa đơn
            String sqlDetail = "INSERT INTO invoice_details (invoice_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            String sqlUpdateStock = "UPDATE products SET stock = stock - ? WHERE product_id = ?";

            try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail);
                 PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock)) {

                for (InvoiceDetail item : details) {
                    pstmtStock.setInt(1, item.getQuantity());
                    pstmtStock.setInt(2, item.getProductID());
                    pstmtStock.executeUpdate();

                    pstmtDetail.setInt(1, invoiceId);
                    pstmtDetail.setInt(2, item.getProductID());
                    pstmtDetail.setInt(3, item.getQuantity());
                    pstmtDetail.setDouble(4, item.getUnitPrice());
                    pstmtDetail.executeUpdate();
                }
            }

            conn.commit();
            return null; // Không có lỗi -> Thành công

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "Lỗi CSDL PostgreSQL: " + e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void displayAllInvoices() {
        String sql = "SELECT i.invoice_id, e.employee_name, c.customer_name, i.created_at, i.total_amount " +
                "FROM invoices i " +
                "JOIN employees e ON i.employee_id = e.employee_id " +
                "JOIN customers c ON i.customer_id = c.customer_id " +
                "ORDER BY i.invoice_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n------------------------------------ DANH SÁCH HÓA ĐƠN ------------------------------------");
            System.out.printf("%-8s | %-22s | %-22s | %-12s | %-15s\n", "Mã HĐ", "Nhân viên", "Khách hàng", "Ngày tạo", "Tổng tiền (VND)");
            System.out.println("-------------------------------------------------------------------------------------------");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.printf("%-8d | %-22s | %-22s | %-12s | %-15.2f\n",
                        rs.getInt("invoice_id"),
                        rs.getString("employee_name"),
                        rs.getString("customer_name"),
                        rs.getDate("created_at"),
                        rs.getDouble("total_amount"));
            }

            if (!hasData) {
                System.out.println("Chưa có hóa đơn nào trong hệ thống.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getRevenueByMonthAndYear(int month, int year) {
        String sql = "SELECT SUM(total_amount) AS revenue FROM invoices " +
                "WHERE EXTRACT(MONTH FROM created_at) = ? AND EXTRACT(YEAR FROM created_at) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("revenue");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean deleteInvoice(int invoiceId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlGetDetails = "SELECT product_id, quantity FROM invoice_details WHERE invoice_id = ?";
            String sqlRestoreStock = "UPDATE products SET stock = stock + ? WHERE product_id = ?";

            try (PreparedStatement pstmtGet = conn.prepareStatement(sqlGetDetails);
                 PreparedStatement pstmtRestore = conn.prepareStatement(sqlRestoreStock)) {

                pstmtGet.setInt(1, invoiceId);
                try (ResultSet rs = pstmtGet.executeQuery()) {
                    boolean hasItems = false;
                    while (rs.next()) {
                        hasItems = true;
                        int productId = rs.getInt("product_id");
                        int quantity = rs.getInt("quantity");

                        pstmtRestore.setInt(1, quantity);
                        pstmtRestore.setInt(2, productId);
                        pstmtRestore.executeUpdate();
                    }

                    if (!hasItems) {
                        String sqlCheck = "SELECT invoice_id FROM invoices WHERE invoice_id = ?";
                        try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                            pstmtCheck.setInt(1, invoiceId);
                            try (ResultSet rsCheck = pstmtCheck.executeQuery()) {
                                if (!rsCheck.next()) {
                                    conn.rollback();
                                    return false;
                                }
                            }
                        }
                    }
                }
            }

            String sqlDeleteDetails = "DELETE FROM invoice_details WHERE invoice_id = ?";
            try (PreparedStatement pstmtDelDetails = conn.prepareStatement(sqlDeleteDetails)) {
                pstmtDelDetails.setInt(1, invoiceId);
                pstmtDelDetails.executeUpdate();
            }

            String sqlDeleteInvoice = "DELETE FROM invoices WHERE invoice_id = ?";
            try (PreparedStatement pstmtDelInv = conn.prepareStatement(sqlDeleteInvoice)) {
                pstmtDelInv.setInt(1, invoiceId);
                int rowsDeleted = pstmtDelInv.executeUpdate();
                if (rowsDeleted == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void displayInvoicesByCustomerName(String customerName) {
        String sql = "SELECT i.invoice_id, e.employee_name, c.customer_name, i.created_at, i.total_amount " +
                "FROM invoices i " +
                "JOIN employees e ON i.employee_id = e.employee_id " +
                "JOIN customers c ON i.customer_id = c.customer_id " +
                "WHERE LOWER(TRIM(c.customer_name)) = LOWER(?) " +
                "ORDER BY i.invoice_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerName.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n-------------------------------- KẾT QUẢ TÌM KIẾM HÓA ĐƠN --------------------------------");
                System.out.printf("%-8s | %-22s | %-22s | %-12s | %-15s\n", "Mã HĐ", "Nhân viên", "Khách hàng", "Ngày tạo", "Tổng tiền (VND)");
                System.out.println("-------------------------------------------------------------------------------------------");

                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    System.out.printf("%-8d | %-22s | %-22s | %-12s | %-15.2f\n",
                            rs.getInt("invoice_id"),
                            rs.getString("employee_name"),
                            rs.getString("customer_name"),
                            rs.getDate("created_at"),
                            rs.getDouble("total_amount"));
                }

                if (!hasData) {
                    System.out.println("Không tìm thấy hóa đơn nào của khách hàng: \"" + customerName.trim() + "\"");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> getAllInvoicesData() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT i.invoice_id, e.employee_name, c.customer_name, i.created_at, i.total_amount " +
                "FROM invoices i " +
                "JOIN employees e ON i.employee_id = e.employee_id " +
                "JOIN customers c ON i.customer_id = c.customer_id " +
                "ORDER BY i.invoice_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("invoice_id"),
                        rs.getString("employee_name"),
                        rs.getString("customer_name"),
                        rs.getDate("created_at"),
                        String.format("%,.2f VNĐ", rs.getDouble("total_amount"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getInvoicesByCustomerName(String customerName) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT i.invoice_id, e.employee_name, c.customer_name, i.created_at, i.total_amount " +
                "FROM invoices i " +
                "JOIN employees e ON i.employee_id = e.employee_id " +
                "JOIN customers c ON i.customer_id = c.customer_id " +
                "WHERE LOWER(c.customer_name) LIKE LOWER(?) " +
                "ORDER BY i.invoice_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + customerName.trim() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getInt("invoice_id"),
                            rs.getString("employee_name"),
                            rs.getString("customer_name"),
                            rs.getDate("created_at"),
                            String.format("%,.2f VNĐ", rs.getDouble("total_amount"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getInvoiceDetailsByInvoiceId(int invoiceId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_name, id.quantity, id.unit_price, (id.quantity * id.unit_price) AS subtotal " +
                "FROM invoice_details id " +
                "JOIN products p ON id.product_id = p.product_id " +
                "WHERE id.invoice_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            String.format("%,.0f VNĐ", rs.getDouble("unit_price")),
                            String.format("%,.0f VNĐ", rs.getDouble("subtotal"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}