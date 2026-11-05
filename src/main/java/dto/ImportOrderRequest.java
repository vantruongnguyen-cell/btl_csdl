package dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import model.ImportOrderDetail;

import java.util.List;

public class ImportOrderRequest {

    @NotNull(message = "Mã nhà cung cấp không được để trống!")
    @Min(value = 1, message = "Mã nhà cung cấp không hợp lệ!")
    private Integer supplierId;

    @NotNull(message = "Mã nhân viên tạo đơn không được để trống!")
    @Min(value = 1, message = "Mã nhân viên không hợp lệ!")
    private Integer employeeId;

    @NotNull(message = "Danh sách sản phẩm nhập không được để trống!")
    @NotEmpty(message = "Đơn nhập hàng phải có ít nhất 1 sản phẩm!")
    @Valid
    private List<ImportOrderDetail> details;

    public ImportOrderRequest() {}

    public ImportOrderRequest(Integer supplierId, Integer employeeId, List<ImportOrderDetail> details) {
        this.supplierId = supplierId;
        this.employeeId = employeeId;
        this.details = details;
    }

    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public List<ImportOrderDetail> getDetails() { return details; }
    public void setDetails(List<ImportOrderDetail> details) { this.details = details; }
}