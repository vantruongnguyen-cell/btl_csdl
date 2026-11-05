package controller;

import dto.InvoiceRequest;
import jakarta.validation.Valid;
import service.InvoiceService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService = new InvoiceService();

    // 1. Lấy danh sách hóa đơn (có thể lọc theo tên khách hàng ?customerName=...)
    @GetMapping
    public List<Object[]> getInvoices(@RequestParam(required = false) String customerName) {
        if (customerName != null && !customerName.trim().isEmpty()) {
            return invoiceService.getInvoicesByCustomerName(customerName);
        }
        return invoiceService.getAllInvoicesData();
    }

    // 2. Lấy chi tiết danh sách sản phẩm của 1 hóa đơn theo ID
    @GetMapping("/{id}/details")
    public List<Object[]> getInvoiceDetails(@PathVariable int id) {
        return invoiceService.getInvoiceDetailsByInvoiceId(id);
    }

    // 3. Thêm mới hóa đơn
    @PostMapping
    public Map<String, Object> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        Map<String, Object> response = new HashMap<>();

        String errorMsg = invoiceService.createInvoice(
                request.getEmployeeId(),
                request.getCustomerId(),
                request.getDetails()
        );

        if (errorMsg == null) {
            response.put("success", true);
            response.put("message", "Tạo hóa đơn thành công! Đã tự động trừ tồn kho.");
        } else {
            response.put("success", false);
            response.put("message", errorMsg); // Trả về thông báo lỗi chi tiết theo nghiệp vụ
        }
        return response;
    }

    // 4. Xóa hóa đơn theo ID (Tự động cộng hoàn trả lại tồn kho sản phẩm)
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteInvoice(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = invoiceService.deleteInvoice(id);
        response.put("success", success);
        response.put("message", success ? "Xóa hóa đơn thành công! Đã tự động hoàn trả tồn kho." : "Xóa hóa đơn thất bại!");
        return response;
    }
}