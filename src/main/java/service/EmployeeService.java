package service;

import dao.EmployeeDAO;
import model.Employee;

import java.util.List;

public class EmployeeService {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public boolean existsById(int id) {
        return employeeDAO.getEmployeeById(id) != null;
    }

    public boolean addEmployee(String name, String phone, String email, String position, double salary) {
        if (salary <= 0) {
            System.out.println("Lỗi: Lương phải lớn hơn 0!");
            return false;
        }
        boolean success = employeeDAO.addEmployee(new Employee(name, phone, email, position, salary));
        if (success) {
            System.out.println("Thêm nhân viên thành công!");
        } else {
            System.out.println("Thêm nhân viên thất bại!");
        }
        return success;
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    public Employee getEmployeeById(int id) {
        return employeeDAO.getEmployeeById(id);
    }

    public List<Employee> getEmployeesByPhone(String phone) {
        return employeeDAO.searchByPhone(phone);
    }

    public boolean updateEmployee(int id, String name, String phone, String email, String position, double salary) {
        if (salary <= 0) {
            System.out.println("Lỗi: Lương phải lớn hơn 0!");
            return false;
        }
        boolean success = employeeDAO.updateEmployee(new Employee(id, name, phone, email, position, salary));
        if (success) {
            System.out.println("Cập nhật thông tin nhân viên thành công!");
        } else {
            System.out.println("Cập nhật thông tin nhân viên thất bại!");
        }
        return success;
    }

    public boolean deleteEmployee(int id) {
        boolean success = employeeDAO.deleteEmployee(id);
        if (success) {
            System.out.println("Xóa nhân viên thành công!");
        } else {
            System.out.println("Xóa nhân viên thất bại!");
        }
        return success;
    }

    public void displayAllEmployees() {
        List<Employee> list = employeeDAO.getAllEmployees();
        if (list.isEmpty()) {
            System.out.println("Danh sách nhân viên trống.");
            return;
        }
        System.out.println("\n------------------------------------------------ DANH SÁCH NHÂN VIÊN ------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-12s | %-25s | %-15s | %-15s\n", "ID", "Tên NV", "SĐT", "Email", "Chức vụ", "Lương (VND)");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        for (Employee e : list) {
            System.out.printf("%-5d | %-20s | %-12s | %-25s | %-15s | %-15.2f\n",
                    e.getEmployeeID(), e.getEmployeeName(), e.getPhone(), e.getEmail(),
                    e.getPosition(), e.getSalary());
        }
    }

    public void searchByPhone(String phone) {
        List<Employee> list = employeeDAO.searchByPhone(phone);
        if (list.isEmpty()) {
            System.out.println("Không tìm thấy nhân viên nào có số điện thoại: " + phone);
            return;
        }
        System.out.println("\n------------------------------------------------ KẾT QUẢ TÌM KIẾM ------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-12s | %-25s | %-15s | %-15s\n", "ID", "Tên NV", "SĐT", "Email", "Chức vụ", "Lương (VND)");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        for (Employee e : list) {
            System.out.printf("%-5d | %-20s | %-12s | %-25s | %-15s | %-15.2f\n",
                    e.getEmployeeID(), e.getEmployeeName(), e.getPhone(), e.getEmail(),
                    e.getPosition(), e.getSalary());
        }
    }
}