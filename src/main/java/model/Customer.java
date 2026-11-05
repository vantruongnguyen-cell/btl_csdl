package model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Customer {
    private int customerId;

    @NotBlank(message = "Tên khách hàng không được để trống!")
    private String customerName;

    @NotBlank(message = "Số điện thoại không được để trống!")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng Việt Nam!")
    private String phone;

    @Email(message = "Email không đúng định dạng hợp lệ!")
    private String email;

    private String address;

    // Constructor rỗng cho Jackson Framework
    public Customer() {}

    public Customer(int customerId, String customerName, String phone, String email, String address) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public Customer(String customerName, String phone, String email, String address) {
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}