package dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import model.InvoiceDetail;

import java.util.List;

public class InvoiceRequest {

    @NotNull(message = "Mã nhân viên lập hóa đơn không được để trống!")
    @Min(value = 1, message = "Mã nhân viên không hợp lệ!")
    private Integer employeeId;

    @NotNull(message = "Mã khách hàng không được để trống!")
    @Min(value = 1, message = "Mã khách hàng không hợp lệ!")
    private Integer customerId;

    @NotNull(message = "Danh sách chi tiết hóa đơn không được để trống!")
    @NotEmpty(message = "Hóa đơn phải có ít nhất 1 sản phẩm!")
    @Valid // Tự động duyệt và kích hoạt validate cho từng item InvoiceDetail trong danh sách
    private List<InvoiceDetail> details;

    public InvoiceRequest() {}

    public InvoiceRequest(Integer employeeId, Integer customerId, List<InvoiceDetail> details) {
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.details = details;
    }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public List<InvoiceDetail> getDetails() { return details; }
    public void setDetails(List<InvoiceDetail> details) { this.details = details; }
}