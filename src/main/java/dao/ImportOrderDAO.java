package dao;

import config.DatabaseConnection;
import model.ImportOrderDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImportOrderDAO {

    // Trả về String: null nếu THÀNH CÔNG, hoặc câu thông báo lỗi CỤ THỂ nếu THẤT BẠI
    public String createImportOrder(int supplierId, int employeeId, List<ImportOrderDetail> details) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra tồn tại Mã Nhà Cung Cấp
            String checkSupSql = "SELECT supplier_id FROM suppliers WHERE supplier_id = ?";
            try (PreparedStatement pstmtSup = conn.prepareStatement(checkSupSql)) {
                pstmtSup.setInt(1, supplierId);
                try (ResultSet rsSup = pstmtSup.executeQuery()) {
                    if (!rsSup.next()) {
                        conn.rollback();
                        return "Mã nhà cung cấp #" + supplierId + " không tồn tại trong hệ thống!";
                    }
                }
            }

            // 2. Kiểm tra tồn tại Mã Nhân Viên
            String checkEmpSql = "SELECT employee_id FROM employees WHERE employee_id = ?";
            try (PreparedStatement pstmtEmp = conn.prepareStatement(checkEmpSql)) {
                pstmtEmp.setInt(1, employeeId);
                try (ResultSet rsEmp = pstmtEmp.executeQuery()) {
                    if (!rsEmp.next()) {
                        conn.rollback();
                        return "Mã nhân viên #" + employeeId + " không tồn tại trong hệ thống!";
                    }
                }
            }

            // 3. Kiểm tra từng sản phẩm nhập có tồn tại trong hệ thống hay không
            String checkProdSql = "SELECT product_name FROM products WHERE product_id = ?";
            for (ImportOrderDetail item : details) {
                try (PreparedStatement pstmtProd = conn.prepareStatement(checkProdSql)) {
                    pstmtProd.setInt(1, item.getProductID());
                    try (ResultSet rsProd = pstmtProd.executeQuery()) {
                        if (!rsProd.next()) {
                            conn.rollback();
                            return "Sản phẩm mã #" + item.getProductID() + " không tồn tại trong hệ thống!";
                        }
                    }
                }
            }

            // 4. Tính tổng tiền nhập
            double totalAmount = 0;
            for (ImportOrderDetail item : details) {
                totalAmount += item.getQuantity() * item.getImportPrice();
            }

            // 5. Thêm đơn nhập hàng chính
            String sqlImport = "INSERT INTO import_orders (supplier_id, employee_id, total_amount) VALUES (?, ?, ?) RETURNING import_id";
            int importId = 0;
            try (PreparedStatement pstmtImp = conn.prepareStatement(sqlImport)) {
                pstmtImp.setInt(1, supplierId);
                pstmtImp.setInt(2, employeeId);
                pstmtImp.setDouble(3, totalAmount);
                try (ResultSet rsImp = pstmtImp.executeQuery()) {
                    if (rsImp.next()) {
                        importId = rsImp.getInt(1);
                    }
                }
            }

            if (importId == 0) {
                conn.rollback();
                return "Không thể khởi tạo mã đơn nhập hàng!";
            }

            // 6. Thêm chi tiết đơn nhập & Cộng dồn số lượng vào tồn kho
            String sqlDetail = "INSERT INTO import_order_details (import_id, product_id, quantity, import_price) VALUES (?, ?, ?, ?)";
            String sqlUpdateStock = "UPDATE products SET stock = stock + ? WHERE product_id = ?";

            try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail);
                 PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock)) {

                for (ImportOrderDetail item : details) {
                    // Thêm chi tiết
                    pstmtDetail.setInt(1, importId);
                    pstmtDetail.setInt(2, item.getProductID());
                    pstmtDetail.setInt(3, item.getQuantity());
                    pstmtDetail.setDouble(4, item.getImportPrice());
                    pstmtDetail.executeUpdate();

                    // Cộng tồn kho
                    pstmtStock.setInt(1, item.getQuantity());
                    pstmtStock.setInt(2, item.getProductID());
                    pstmtStock.executeUpdate();
                }
            }

            conn.commit(); // Hoàn tất giao dịch
            return null; // Không có lỗi -> THÀNH CÔNG

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

    // Hiển thị tất cả đơn nhập hàng (kèm danh sách chi tiết sản phẩm - Console)
    public void displayAllImportOrders() {
        String sqlOrder = "SELECT io.import_id, s.supplier_name, e.employee_name, io.created_at, io.total_amount " +
                "FROM import_orders io " +
                "JOIN suppliers s ON io.supplier_id = s.supplier_id " +
                "JOIN employees e ON io.employee_id = e.employee_id " +
                "ORDER BY io.import_id DESC";

        String sqlDetail = "SELECT p.product_name, iod.quantity, iod.import_price, (iod.quantity * iod.import_price) AS subtotal " +
                "FROM import_order_details iod " +
                "JOIN products p ON iod.product_id = p.product_id " +
                "WHERE iod.import_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rsOrder = stmt.executeQuery(sqlOrder)) {

            System.out.println("\n=================================== DANH SÁCH ĐƠN NHẬP HÀNG ===================================");
            boolean hasData = false;

            while (rsOrder.next()) {
                hasData = true;
                int importId = rsOrder.getInt("import_id");
                System.out.printf("\n[MÃ ĐƠN: %d] | NCC: %s | NV Nhập: %s | Ngày tạo: %s\n",
                        importId,
                        rsOrder.getString("supplier_name"),
                        rsOrder.getString("employee_name"),
                        rsOrder.getDate("created_at"));
                System.out.println("-----------------------------------------------------------------------------------------------");
                System.out.printf("  %-30s | %-10s | %-15s | %-15s\n", "Tên sản phẩm", "Số lượng", "Đơn giá (VND)", "Thành tiền (VND)");
                System.out.println("  ---------------------------------------------------------------------------------------------");

                try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                    pstmtDetail.setInt(1, importId);
                    try (ResultSet rsDetail = pstmtDetail.executeQuery()) {
                        while (rsDetail.next()) {
                            System.out.printf("  %-30s | %-10d | %-15.2f | %-15.2f\n",
                                    rsDetail.getString("product_name"),
                                    rsDetail.getInt("quantity"),
                                    rsDetail.getDouble("import_price"),
                                    rsDetail.getDouble("subtotal"));
                        }
                    }
                }
                System.out.printf("==> TỔNG TIỀN ĐƠN NHẬP: %,.2f VNĐ\n", rsOrder.getDouble("total_amount"));
                System.out.println("===============================================================================================");
            }

            if (!hasData) {
                System.out.println("Chưa có đơn nhập hàng nào trong hệ thống.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hiển thị danh sách đơn nhập hàng theo Mã nhà cung cấp (Console)
    public void displayImportOrdersBySupplierId(int supplierId) {
        String sqlOrder = "SELECT io.import_id, s.supplier_name, e.employee_name, io.created_at, io.total_amount " +
                "FROM import_orders io " +
                "JOIN suppliers s ON io.supplier_id = s.supplier_id " +
                "JOIN employees e ON io.employee_id = e.employee_id " +
                "WHERE io.supplier_id = ? " +
                "ORDER BY io.import_id DESC";

        String sqlDetail = "SELECT p.product_name, iod.quantity, iod.import_price, (iod.quantity * iod.import_price) AS subtotal " +
                "FROM import_order_details iod " +
                "JOIN products p ON iod.product_id = p.product_id " +
                "WHERE iod.import_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder)) {

            pstmtOrder.setInt(1, supplierId);

            try (ResultSet rsOrder = pstmtOrder.executeQuery()) {
                System.out.println("\n============================= KẾT QUẢ LỌC ĐƠN NHẬP (NCC ID: " + supplierId + ") =============================");
                boolean hasData = false;

                while (rsOrder.next()) {
                    hasData = true;
                    int importId = rsOrder.getInt("import_id");
                    System.out.printf("\n[MÃ ĐƠN: %d] | NCC: %s | NV Nhập: %s | Ngày tạo: %s\n",
                            importId,
                            rsOrder.getString("supplier_name"),
                            rsOrder.getString("employee_name"),
                            rsOrder.getDate("created_at"));
                    System.out.println("-----------------------------------------------------------------------------------------------");
                    System.out.printf("  %-30s | %-10s | %-15s | %-15s\n", "Tên sản phẩm", "Số lượng", "Đơn giá (VND)", "Thành tiền (VND)");
                    System.out.println("  ---------------------------------------------------------------------------------------------");

                    try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                        pstmtDetail.setInt(1, importId);
                        try (ResultSet rsDetail = pstmtDetail.executeQuery()) {
                            while (rsDetail.next()) {
                                System.out.printf("  %-30s | %-10d | %-15.2f | %-15.2f\n",
                                        rsDetail.getString("product_name"),
                                        rsDetail.getInt("quantity"),
                                        rsDetail.getDouble("import_price"),
                                        rsDetail.getDouble("subtotal"));
                            }
                        }
                    }
                    System.out.printf("==> TỔNG TIỀN ĐƠN NHẬP: %,.2f VNĐ\n", rsOrder.getDouble("total_amount"));
                    System.out.println("===============================================================================================");
                }

                if (!hasData) {
                    System.out.println("Không tìm thấy đơn nhập hàng nào của Nhà cung cấp ID = " + supplierId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa đơn nhập hàng (Trừ lại số lượng đã cộng vào kho)
    public boolean deleteImportOrder(int importId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra tồn kho hiện tại xem có đủ để trừ lại không
            String sqlGetDetails = "SELECT iod.product_id, iod.quantity, p.stock " +
                    "FROM import_order_details iod " +
                    "JOIN products p ON iod.product_id = p.product_id " +
                    "WHERE iod.import_id = ?";

            String sqlDeductStock = "UPDATE products SET stock = stock - ? WHERE product_id = ?";

            try (PreparedStatement pstmtGet = conn.prepareStatement(sqlGetDetails);
                 PreparedStatement pstmtDeduct = conn.prepareStatement(sqlDeductStock)) {

                pstmtGet.setInt(1, importId);
                try (ResultSet rs = pstmtGet.executeQuery()) {
                    boolean hasItems = false;
                    while (rs.next()) {
                        hasItems = true;
                        int productId = rs.getInt("product_id");
                        int quantity = rs.getInt("quantity");
                        int currentStock = rs.getInt("stock");

                        if (currentStock < quantity) {
                            conn.rollback();
                            System.out.println("Lỗi: Sản phẩm ID " + productId + " hiện tại trong kho không đủ để trừ hoàn trả đơn nhập này!");
                            return false;
                        }

                        // Trừ lại tồn kho
                        pstmtDeduct.setInt(1, quantity);
                        pstmtDeduct.setInt(2, productId);
                        pstmtDeduct.executeUpdate();
                    }

                    if (!hasItems) {
                        // Kiểm tra đơn nhập có tồn tại hay không
                        String sqlCheck = "SELECT import_id FROM import_orders WHERE import_id = ?";
                        try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                            pstmtCheck.setInt(1, importId);
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

            // 2. Xóa chi tiết đơn nhập
            String sqlDeleteDetails = "DELETE FROM import_order_details WHERE import_id = ?";
            try (PreparedStatement pstmtDelDetails = conn.prepareStatement(sqlDeleteDetails)) {
                pstmtDelDetails.setInt(1, importId);
                pstmtDelDetails.executeUpdate();
            }

            // 3. Xóa đơn nhập chính
            String sqlDeleteImport = "DELETE FROM import_orders WHERE import_id = ?";
            try (PreparedStatement pstmtDelImp = conn.prepareStatement(sqlDeleteImport)) {
                pstmtDelImp.setInt(1, importId);
                int rowsDeleted = pstmtDelImp.executeUpdate();
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

    public List<Object[]> getAllImportOrdersData() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT io.import_id, s.supplier_name, e.employee_name, io.created_at, io.total_amount " +
                "FROM import_orders io " +
                "JOIN suppliers s ON io.supplier_id = s.supplier_id " +
                "JOIN employees e ON io.employee_id = e.employee_id " +
                "ORDER BY io.import_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("import_id"),
                        rs.getString("supplier_name"),
                        rs.getString("employee_name"),
                        rs.getDate("created_at"),
                        String.format("%,.2f VNĐ", rs.getDouble("total_amount"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getImportOrdersBySupplierId(int supplierId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT io.import_id, s.supplier_name, e.employee_name, io.created_at, io.total_amount " +
                "FROM import_orders io " +
                "JOIN suppliers s ON io.supplier_id = s.supplier_id " +
                "JOIN employees e ON io.employee_id = e.employee_id " +
                "WHERE io.supplier_id = ? " +
                "ORDER BY io.import_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getInt("import_id"),
                            rs.getString("supplier_name"),
                            rs.getString("employee_name"),
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

    public List<Object[]> getImportOrderDetailsById(int importOrderId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_name, iod.quantity, iod.import_price, (iod.quantity * iod.import_price) AS subtotal " +
                "FROM import_order_details iod " +
                "LEFT JOIN products p ON iod.product_id = p.product_id " +
                "WHERE iod.import_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, importOrderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String pName = rs.getString("product_name");
                    if (pName == null) pName = "Sản phẩm ID " + rs.getInt("product_id");

                    list.add(new Object[]{
                            pName,
                            rs.getInt("quantity"),
                            String.format("%,.0f VNĐ", rs.getDouble("import_price")),
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