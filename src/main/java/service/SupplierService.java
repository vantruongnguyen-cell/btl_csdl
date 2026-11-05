package service;

import dao.SupplierDAO;
import model.Supplier;

import java.util.List;

public class SupplierService {
    private SupplierDAO supplierDAO = new SupplierDAO();

    public boolean existsById(int id) {
        return supplierDAO.getSupplierById(id) != null;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierDAO.getAllSuppliers();
    }

    public List<Supplier> getSuppliersByPhone(String phone) {
        return supplierDAO.searchByPhone(phone);
    }

    // ======================================================================

    public boolean addSupplier(String name, String phone, String email, String address) {
        boolean success = supplierDAO.addSupplier(new Supplier(name, phone, email, address));
        if (success) {
            System.out.println("Thêm nhà cung cấp thành công!");
        } else {
            System.out.println("Thêm nhà cung cấp thất bại!");
        }
        return success;
    }

    public boolean updateSupplier(int id, String name, String phone, String email, String address) {
        boolean success = supplierDAO.updateSupplier(new Supplier(id, name, phone, email, address));
        if (success) {
            System.out.println("Cập nhật thông tin nhà cung cấp thành công!");
        } else {
            System.out.println("Cập nhật thông tin nhà cung cấp thất bại!");
        }
        return success;
    }

    public boolean deleteSupplier(int id) {
        boolean success = supplierDAO.deleteSupplier(id);
        if (success) {
            System.out.println("Xóa nhà cung cấp thành công!");
        } else {
            System.out.println("Xóa nhà cung cấp thất bại!");
        }
        return success;
    }

    public void displayAllSuppliers() {
        List<Supplier> list = supplierDAO.getAllSuppliers();
        if (list.isEmpty()) {
            System.out.println("Danh sách nhà cung cấp trống.");
            return;
        }
        System.out.println("\n-------------------------------- DANH SÁCH NHÀ CUNG CẤP --------------------------------");
        System.out.printf("%-5s | %-25s | %-12s | %-25s | %-20s\n", "ID", "Tên Nhà Cung Cấp", "SĐT", "Email", "Địa chỉ");
        System.out.println("----------------------------------------------------------------------------------------");
        for (Supplier s : list) {
            System.out.printf("%-5d | %-25s | %-12s | %-25s | %-20s\n",
                    s.getSupplierID(), s.getSupplierName(), s.getPhone(), s.getEmail(), s.getAddress());
        }
    }

    public void searchByPhone(String phone) {
        List<Supplier> list = supplierDAO.searchByPhone(phone);
        if (list.isEmpty()) {
            System.out.println("Không tìm thấy nhà cung cấp nào có số điện thoại: " + phone);
            return;
        }
        System.out.println("\n-------------------------------- KẾT QUẢ TÌM KIẾM --------------------------------");
        System.out.printf("%-5s | %-25s | %-12s | %-25s | %-20s\n", "ID", "Tên Nhà Cung Cấp", "SĐT", "Email", "Địa chỉ");
        System.out.println("----------------------------------------------------------------------------------");
        for (Supplier s : list) {
            System.out.printf("%-5d | %-25s | %-12s | %-25s | %-20s\n",
                    s.getSupplierID(), s.getSupplierName(), s.getPhone(), s.getEmail(), s.getAddress());
        }
    }
}