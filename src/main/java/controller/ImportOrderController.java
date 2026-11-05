package controller;

import dto.ImportOrderRequest;
import jakarta.validation.Valid;
import service.ImportOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import-orders")
@CrossOrigin(origins = "*")
public class ImportOrderController {

    private final ImportOrderService importOrderService = new ImportOrderService();

    // 1. Lấy danh sách tất cả đơn nhập hàng (hoặc lọc theo mã nhà cung cấp ?supplierId=...)
    // GET: http://localhost:8080/api/import-orders
    // GET: http://localhost:8080/api/import-orders?supplierId=1
    @GetMapping
    public List<Object[]> getImportOrders(@RequestParam(required = false) Integer supplierId) {
        if (supplierId != null && supplierId > 0) {
            return importOrderService.getImportOrdersBySupplierId(supplierId);
        }
        return importOrderService.getAllImportOrdersData();
    }

    // 2. Lấy chi tiết các sản phẩm trong 1 đơn nhập hàng theo ID
    // GET: http://localhost:8080/api/import-orders/1/details
    @GetMapping("/{id}/details")
    public List<Object[]> getImportOrderDetails(@PathVariable int id) {
        return importOrderService.getImportOrderDetailsById(id);
    }

    // 3. Tạo mới đơn nhập hàng (Đã bổ sung @Valid để kích hoạt kiểm tra DTO)
    // POST: http://localhost:8080/api/import-orders
    @PostMapping
    public Map<String, Object> createImportOrder(@Valid @RequestBody ImportOrderRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Gọi Service nhận chuỗi thông báo lỗi chi tiết (null nếu thành công)
        String errorMsg = importOrderService.createImportOrder(
                request.getSupplierId(),
                request.getEmployeeId(),
                request.getDetails()
        );

        if (errorMsg == null) {
            response.put("success", true);
            response.put("message", "Tạo đơn nhập hàng thành công! Đã tự động cộng dồn tồn kho.");
        } else {
            response.put("success", false);
            response.put("message", errorMsg); // Trả về câu lỗi cụ thể (sai ID NCC, sai ID NV,...)
        }
        return response;
    }

    // 4. Xóa đơn nhập hàng theo ID (Tự động trừ lại tồn kho tương ứng)
    // DELETE: http://localhost:8080/api/import-orders/1
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteImportOrder(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = importOrderService.deleteImportOrder(id);
        response.put("success", success);
        response.put("message", success ? "Xóa đơn nhập hàng thành công! Đã trừ hoàn trả số lượng trong kho." : "Xóa thất bại! (Không tìm thấy mã đơn hoặc số lượng trong kho hiện tại không đủ để trừ)");
        return response;
    }
}