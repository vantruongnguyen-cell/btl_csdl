package model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Supplier {
    private int supplierID;

    @NotBlank(message = "Tên nhà cung cấp không được để trống!")
    private String supplierName;

    @NotBlank(message = "Số điện thoại nhà cung cấp không được để trống!")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng Việt Nam!")
    private String phone;

    @Email(message = "Email không đúng định dạng hợp lệ!")
    private String email;

    private String address;

    // Constructor rỗng bắt buộc để Spring Boot deserialize JSON từ Web UI (Jackson)
    public Supplier() {}

    public Supplier(String supplierName, String phone, String email, String address) {
        this.supplierName = supplierName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public Supplier(int supplierID, String supplierName, String phone, String email, String address) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}