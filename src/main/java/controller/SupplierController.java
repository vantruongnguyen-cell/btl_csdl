package controller;

import jakarta.validation.Valid;
import model.Supplier;
import service.SupplierService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {

    private final SupplierService supplierService = new SupplierService();

    // 1. Lấy tất cả nhà cung cấp hoặc lọc theo số điện thoại (?phone=...)
    // GET: http://localhost:8080/api/suppliers
    // GET: http://localhost:8080/api/suppliers?phone=0987654321
    @GetMapping
    public List<Supplier> getSuppliers(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return supplierService.getSuppliersByPhone(phone);
        }
        return supplierService.getAllSuppliers();
    }

    // 2. Thêm mới nhà cung cấp (Đã bổ sung @Valid)
    // POST: http://localhost:8080/api/suppliers
    @PostMapping
    public Map<String, Object> addSupplier(@Valid @RequestBody Supplier supplier) {
        Map<String, Object> response = new HashMap<>();
        boolean success = supplierService.addSupplier(
                supplier.getSupplierName(),
                supplier.getPhone(),
                supplier.getEmail(),
                supplier.getAddress()
        );
        response.put("success", success);
        response.put("message", success ? "Thêm nhà cung cấp thành công!" : "Thêm nhà cung cấp thất bại!");
        return response;
    }

    // 3. Cập nhật thông tin nhà cung cấp theo ID (Đã bổ sung @Valid)
    // PUT: http://localhost:8080/api/suppliers/1
    @PutMapping("/{id}")
    public Map<String, Object> updateSupplier(@PathVariable int id, @Valid @RequestBody Supplier supplier) {
        Map<String, Object> response = new HashMap<>();
        boolean success = supplierService.updateSupplier(
                id,
                supplier.getSupplierName(),
                supplier.getPhone(),
                supplier.getEmail(),
                supplier.getAddress()
        );
        response.put("success", success);
        response.put("message", success ? "Cập nhật nhà cung cấp thành công!" : "Cập nhật nhà cung cấp thất bại!");
        return response;
    }

    // 4. Xóa nhà cung cấp theo ID
    // DELETE: http://localhost:8080/api/suppliers/1
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteSupplier(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = supplierService.deleteSupplier(id);
        response.put("success", success);
        response.put("message", success ? "Xóa nhà cung cấp thành công!" : "Xóa nhà cung cấp thất bại!");
        return response;
    }
}