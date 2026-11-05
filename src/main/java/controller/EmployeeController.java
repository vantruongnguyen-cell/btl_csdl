package controller;

import jakarta.validation.Valid;
import model.Employee;
import service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService = new EmployeeService();

    // 1. Lấy tất cả nhân viên hoặc lọc theo SĐT
    // GET: http://localhost:8080/api/employees
    // GET: http://localhost:8080/api/employees?phone=0912345678
    @GetMapping
    public List<Employee> getEmployees(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return employeeService.getEmployeesByPhone(phone);
        }
        return employeeService.getAllEmployees();
    }

    // 2. Lấy chi tiết nhân viên theo ID
    // GET: http://localhost:8080/api/employees/1
    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable int id) {
        return employeeService.getEmployeeById(id);
    }

    // 3. Thêm mới nhân viên (Đã bổ sung @Valid)
    // POST: http://localhost:8080/api/employees
    @PostMapping
    public Map<String, Object> addEmployee(@Valid @RequestBody Employee emp) {
        Map<String, Object> response = new HashMap<>();
        boolean success = employeeService.addEmployee(
                emp.getEmployeeName(),
                emp.getPhone(),
                emp.getEmail(),
                emp.getPosition(),
                emp.getSalary()
        );
        response.put("success", success);
        response.put("message", success ? "Thêm nhân viên thành công!" : "Thêm thất bại!");
        return response;
    }

    // 4. Cập nhật nhân viên theo ID (Đã bổ sung @Valid)
    // PUT: http://localhost:8080/api/employees/1
    @PutMapping("/{id}")
    public Map<String, Object> updateEmployee(@PathVariable int id, @Valid @RequestBody Employee emp) {
        Map<String, Object> response = new HashMap<>();
        boolean success = employeeService.updateEmployee(
                id,
                emp.getEmployeeName(),
                emp.getPhone(),
                emp.getEmail(),
                emp.getPosition(),
                emp.getSalary()
        );
        response.put("success", success);
        response.put("message", success ? "Cập nhật nhân viên thành công!" : "Cập nhật thất bại!");
        return response;
    }

    // 5. Xóa nhân viên theo ID
    // DELETE: http://localhost:8080/api/employees/1
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteEmployee(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = employeeService.deleteEmployee(id);
        response.put("success", success);
        response.put("message", success ? "Xóa nhân viên thành công!" : "Xóa thất bại!");
        return response;
    }
}