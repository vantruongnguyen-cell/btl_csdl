package model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class Product {
    private int productId;

    @NotBlank(message = "Tên sản phẩm không được để trống!")
    private String productName;

    @NotBlank(message = "Thương hiệu không được để trống!")
    private String brand;

    @NotNull(message = "Số lượng tồn kho không được để trống!")
    @PositiveOrZero(message = "Số lượng tồn kho không được nhỏ hơn 0!")
    private Integer stock;

    @NotNull(message = "Giá bán không được để trống!")
    @Positive(message = "Giá bán phải lớn hơn 0!")
    private Double price;

    public Product() {}

    public Product(int productId, String productName, String brand, Double price, Integer stock) {
        this.productId = productId;
        this.productName = productName;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
    }

    public Product(String productName, String brand, Double price, Integer stock) {
        this.productName = productName;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}