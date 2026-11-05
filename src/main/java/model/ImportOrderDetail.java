package model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ImportOrderDetail {
    private int id;
    private int importID;

    @NotNull(message = "Mã sản phẩm không được để trống!")
    @Min(value = 1, message = "Mã sản phẩm không hợp lệ!")
    private Integer productID;

    @NotNull(message = "Số lượng nhập không được để trống!")
    @Positive(message = "Số lượng nhập phải lớn hơn 0!")
    private Integer quantity;

    @NotNull(message = "Giá nhập không được để trống!")
    @Positive(message = "Giá nhập phải lớn hơn 0!")
    private Double importPrice;

    public ImportOrderDetail() {
    }

    public ImportOrderDetail(Integer productID, Integer quantity, Double importPrice) {
        this.productID = productID;
        this.quantity = quantity;
        this.importPrice = importPrice;
    }

    public ImportOrderDetail(int id, int importID, Integer productID, Integer quantity, Double importPrice) {
        this.id = id;
        this.importID = importID;
        this.productID = productID;
        this.quantity = quantity;
        this.importPrice = importPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImportID() {
        return importID;
    }

    public void setImportID(int importID) {
        this.importID = importID;
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

    public Double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(Double importPrice) {
        this.importPrice = importPrice;
    }
}