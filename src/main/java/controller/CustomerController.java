package controller;

import jakarta.validation.Valid;
import model.Customer;
import service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService = new CustomerService();

    // 1. Lấy danh sách hoặc Tìm kiếm theo SĐT
    // GET: http://localhost:8080/api/customers
    @GetMapping
    public List<Customer> getCustomers(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return customerService.getCustomersByPhone(phone);
        }
        return customerService.getAllCustomers();
    }

    // 2. Thêm mới Khách Hàng (Đã bổ sung @Valid)
    // POST: http://localhost:8080/api/customers
    @PostMapping
    public Map<String, Object> addCustomer(@Valid @RequestBody Customer customer) {
        Map<String, Object> response = new HashMap<>();
        boolean success = customerService.addCustomer(
                customer.getCustomerName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress()
        );
        response.put("success", success);
        response.put("message", success ? "Thêm khách hàng thành công!" : "Thêm thất bại!");
        return response;
    }

    // 3. Cập nhật Khách Hàng theo ID (Đã bổ sung @Valid)
    // PUT: http://localhost:8080/api/customers/1
    @PutMapping("/{id}")
    public Map<String, Object> updateCustomer(@PathVariable int id, @Valid @RequestBody Customer customer) {
        Map<String, Object> response = new HashMap<>();
        boolean success = customerService.updateCustomer(
                id,
                customer.getCustomerName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress()
        );
        response.put("success", success);
        response.put("message", success ? "Cập nhật thành công!" : "Cập nhật thất bại!");
        return response;
    }

    // 4. Xóa Khách Hàng theo ID
    // DELETE: http://localhost:8080/api/customers/1
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteCustomer(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = customerService.deleteCustomer(id);
        response.put("success", success);
        response.put("message", success ? "Xóa khách hàng thành công!" : "Xóa thất bại!");
        return response;
    }
}