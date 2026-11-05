package model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class InvoiceDetail {
    private int id;
    private int invoiceID;

    @NotNull(message = "Mã sản phẩm không được để trống!")
    @Min(value = 1, message = "Mã sản phẩm không hợp lệ!")
    private Integer productID;

    @NotNull(message = "Số lượng mua không được để trống!")
    @Positive(message = "Số lượng mua phải lớn hơn 0!")
    private Integer quantity;

    @NotNull(message = "Đơn giá không được để trống!")
    @Positive(message = "Đơn giá phải lớn hơn 0!")
    private Double unitPrice;

    public InvoiceDetail() {}

    // 1. Constructor dùng khi tạo mới trong giỏ hàng (truyền productID, quantity, unitPrice)
    public InvoiceDetail(Integer productID, Integer quantity, Double unitPrice) {
        this.productID = productID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // 2. Constructor dùng khi đọc dữ liệu đầy đủ từ DB
    public InvoiceDetail(int id, int invoiceID, Integer productID, Integer quantity, Double unitPrice) {
        this.id = id;
        this.invoiceID = invoiceID;
        this.productID = productID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}