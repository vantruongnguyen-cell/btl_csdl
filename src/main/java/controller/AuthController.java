package controller;

import dto.LoginRequest;
import jakarta.validation.Valid;
import service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService = new AuthService();

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        boolean isAuthenticated = authService.login(request.getUsername(), request.getPassword());

        if (isAuthenticated) {
            response.put("success", true);
            response.put("message", "Đăng nhập thành công!");
            response.put("username", request.getUsername());
        } else {
            response.put("success", false);
            response.put("message", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        }

        return response;
    }
}