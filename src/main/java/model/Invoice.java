package model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.sql.Date;

public class Invoice {
    private int invoiceId;

    @NotNull(message = "Mã nhân viên lập hóa đơn không được để trống!")
    @Min(value = 1, message = "Mã nhân viên không hợp lệ!")
    private Integer employeeId;

    @NotNull(message = "Mã khách hàng không được để trống!")
    @Min(value = 1, message = "Mã khách hàng không hợp lệ!")
    private Integer customerId;

    private Date createdAt;

    @NotNull(message = "Tổng tiền hóa đơn không được để trống!")
    @Min(value = 0, message = "Tổng tiền hóa đơn không được nhỏ hơn 0!")
    private Double totalAmount;

    public Invoice() {}

    public Invoice(int invoiceId, Integer employeeId, Integer customerId, Date createdAt, Double totalAmount) {
        this.invoiceId = invoiceId;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
    }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
}