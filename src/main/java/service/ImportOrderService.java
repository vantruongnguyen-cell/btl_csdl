package service;

import dao.ImportOrderDAO;
import model.ImportOrderDetail;

import java.util.List;

public class ImportOrderService {
    private ImportOrderDAO importOrderDAO = new ImportOrderDAO();

    // Trả về String: null nếu THÀNH CÔNG, hoặc câu thông báo lỗi CỤ THỂ nếu THẤT BẠI
    public String createImportOrder(int supplierId, int employeeId, List<ImportOrderDetail> details) {
        if (details == null || details.isEmpty()) {
            return "Chi tiết đơn nhập hàng không được để trống!";
        }

        String errorMsg = importOrderDAO.createImportOrder(supplierId, employeeId, details);
        if (errorMsg == null) {
            System.out.println("\n==> TẠO ĐƠN NHẬP HÀNG THÀNH CÔNG! Số lượng đã được cộng dồn vào kho.");
        } else {
            System.out.println("\n==> TẠO ĐƠN NHẬP HÀNG THẤT BẠI: " + errorMsg);
        }
        return errorMsg;
    }

    public boolean deleteImportOrder(int importId) {
        boolean success = importOrderDAO.deleteImportOrder(importId);
        if (success) {
            System.out.println("Xóa đơn nhập hàng ID " + importId + " thành công! Đã trừ lại số lượng tương ứng khỏi kho.");
        } else {
            System.out.println("Xóa đơn nhập hàng thất bại! (Mã đơn không tồn tại hoặc hàng kho không đủ để trừ)");
        }
        return success;
    }

    public List<Object[]> getAllImportOrdersData() {
        return importOrderDAO.getAllImportOrdersData();
    }

    public List<Object[]> getImportOrdersBySupplierId(int supplierId) {
        return importOrderDAO.getImportOrdersBySupplierId(supplierId);
    }

    public void displayAllImportOrders() {
        importOrderDAO.displayAllImportOrders();
    }

    public void displayImportOrdersBySupplierId(int supplierId) {
        importOrderDAO.displayImportOrdersBySupplierId(supplierId);
    }

    public List<Object[]> getImportOrderDetailsById(int importOrderId) {
        return importOrderDAO.getImportOrderDetailsById(importOrderId);
    }
}