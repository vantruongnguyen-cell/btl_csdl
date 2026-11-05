package model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class Employee {
    private int employeeID;

    @NotBlank(message = "Tên nhân viên không được để trống!")
    private String employeeName;

    @NotBlank(message = "Số điện thoại không được để trống!")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng Việt Nam!")
    private String phone;

    @Email(message = "Email không đúng định dạng hợp lệ!")
    private String email;

    @NotBlank(message = "Chức vụ không được để trống!")
    private String position;

    @NotNull(message = "Mức lương không được để trống!")
    @Positive(message = "Mức lương phải lớn hơn 0!")
    private Double salary;

    // Constructor rỗng bắt buộc cho Spring Boot Web API (Jackson)
    public Employee() {}

    public Employee(String employeeName, String phone, String email, String position, Double salary) {
        this.employeeName = employeeName;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.salary = salary;
    }

    public Employee(int employeeID, String employeeName, String phone, String email, String position, Double salary) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.salary = salary;
    }

    public int getEmployeeID() { return employeeID; }
    public void setEmployeeID(int employeeID) { this.employeeID = employeeID; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
}