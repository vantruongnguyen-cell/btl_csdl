package model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class ImportOrder {
    private int importID;

    @NotNull(message = "Mã nhà cung cấp không được để trống!")
    @Min(value = 1, message = "Mã nhà cung cấp không hợp lệ!")
    private Integer supplierID;

    @NotNull(message = "Mã nhân viên tạo đơn không được để trống!")
    @Min(value = 1, message = "Mã nhân viên không hợp lệ!")
    private Integer employeeID;

    private Date created_at;

    @NotNull(message = "Tổng tiền đơn nhập không được để trống!")
    @Min(value = 0, message = "Tổng tiền đơn nhập không được nhỏ hơn 0!")
    private Double total_amount;

    public ImportOrder() {
    }

    public ImportOrder(int importID, Integer supplierID, Integer employeeID, Date created_at, Double total_amount) {
        this.importID = importID;
        this.supplierID = supplierID;
        this.employeeID = employeeID;
        this.created_at = created_at;
        this.total_amount = total_amount;
    }

    public int getImportID() {
        return importID;
    }

    public void setImportID(int importID) {
        this.importID = importID;
    }

    public Integer getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(Integer supplierID) {
        this.supplierID = supplierID;
    }

    public Integer getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Integer employeeID) {
        this.employeeID = employeeID;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }
}