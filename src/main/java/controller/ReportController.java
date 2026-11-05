package controller;

import service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService = new ReportService();

    // 1. Lấy báo cáo Doanh thu & Lợi nhuận của 1 tháng cụ thể trong năm
    // GET: http://localhost:8080/api/reports/monthly?month=10&year=2026
    @GetMapping("/monthly")
    public Map<String, Object> getRevenueAndProfitByMonth(@RequestParam int month, @RequestParam int year) {
        Map<String, Object> response = new HashMap<>();
        if (month < 1 || month > 12) {
            response.put("success", false);
            response.put("message", "Tháng không hợp lệ (phải từ 1 đến 12)!");
            return response;
        }

        Object[] data = reportService.getRevenueAndProfitByMonthData(month, year);
        response.put("success", true);
        response.put("data", data);
        return response;
    }

    // 2. Lấy dữ liệu Doanh thu & Lợi nhuận trọn bộ 12 tháng theo năm
    // GET: http://localhost:8080/api/reports/yearly?year=2026
    @GetMapping("/yearly")
    public List<Object[]> getRevenueAndProfitByYear(@RequestParam int year) {
        return reportService.getRevenueAndProfitByYearData(year);
    }

    // 3. Lấy báo cáo Doanh số bán hàng theo từng Nhân viên trong năm
    // GET: http://localhost:8080/api/reports/employees?year=2026
    @GetMapping("/employees")
    public List<Object[]> getRevenueByEmployee(@RequestParam int year) {
        return reportService.getRevenueByEmployeeData(year);
    }

    // 4. Lấy danh sách TOP 5 sản phẩm bán chạy nhất
    // GET: http://localhost:8080/api/reports/top-selling
    @GetMapping("/top-selling")
    public List<Object[]> getTopSellingProducts() {
        return reportService.getTopSellingProductsData();
    }

    // 5. Cảnh báo danh sách sản phẩm tồn kho dưới ngưỡng chỉ định (Mặc định ngưỡng = 10 nếu không truyền)
    // GET: http://localhost:8080/api/reports/low-stock
    // GET: http://localhost:8080/api/reports/low-stock?threshold=5
    @GetMapping("/low-stock")
    public List<Object[]> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        return reportService.getLowStockProductsData(threshold);
    }
}