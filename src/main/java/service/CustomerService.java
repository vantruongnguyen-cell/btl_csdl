package service;

import dao.CustomerDAO;
import model.Customer;

import java.util.List;

public class CustomerService {
    private CustomerDAO customerDAO = new CustomerDAO();

    public boolean existsById(int id) {
        return customerDAO.getCustomerById(id) != null;
    }

    public boolean addCustomer(String name, String phone, String email, String address) {
        Customer c = new Customer(name, phone, email, address);
        boolean success = customerDAO.addCustomer(c);
        if (success) {
            System.out.println("Thêm khách hàng thành công!");
        } else {
            System.out.println("Thêm khách hàng thất bại!");
        }
        return success;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public List<Customer> getCustomersByPhone(String phone) {
        return customerDAO.searchByPhone(phone);
    }
    public boolean updateCustomer(int id, String name, String phone, String email, String address) {
        Customer c = new Customer(id, name, phone, email, address);
        return customerDAO.updateCustomer(c);
    }

    public boolean deleteCustomer(int id) {
        return customerDAO.deleteCustomer(id);
    }
    public void displayAllCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("Danh sách khách hàng trống.");
            return;
        }
        System.out.println("\n-------------------------------- DANH SÁCH KHÁCH HÀNG --------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-25s | %-20s\n", "ID", "Tên khách hàng", "SĐT", "Email", "Địa chỉ");
        System.out.println("--------------------------------------------------------------------------------------");
        for (Customer c : customers) {
            System.out.printf("%-5d | %-20s | %-15s | %-25s | %-20s\n",
                    c.getCustomerId(), c.getCustomerName(), c.getPhone(), c.getEmail(), c.getAddress());
        }
    }

    public void searchByPhone(String phone) {
        List<Customer> customers = customerDAO.searchByPhone(phone);
        if (customers.isEmpty()) {
            System.out.println("Không tìm thấy khách hàng nào có số điện thoại: " + phone);
            return;
        }
        System.out.println("\n-------------------------------- KẾT QUẢ TÌM KIẾM --------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-25s | %-20s\n", "ID", "Tên khách hàng", "SĐT", "Email", "Địa chỉ");
        System.out.println("----------------------------------------------------------------------------------");
        for (Customer c : customers) {
            System.out.printf("%-5d | %-20s | %-15s | %-25s | %-20s\n",
                    c.getCustomerId(), c.getCustomerName(), c.getPhone(), c.getEmail(), c.getAddress());
        }
    }
}