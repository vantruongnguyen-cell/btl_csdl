package controller;

import jakarta.validation.Valid;
import model.Product;
import service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService = new ProductService();

    // 1. Lấy tất cả sản phẩm hoặc lọc theo hãng sản xuất (brand)
    // GET: http://localhost:8080/api/products
    // GET: http://localhost:8080/api/products?brand=Apple
    @GetMapping
    public List<Product> getProducts(@RequestParam(required = false) String brand) {
        if (brand != null && !brand.trim().isEmpty()) {
            return productService.getProductsByBrand(brand);
        }
        return productService.getAllProducts();
    }

    // 2. Lấy giá của sản phẩm theo ID (hỗ trợ cho màn hình bán hàng/tính hóa đơn)
    // GET: http://localhost:8080/api/products/1/price
    @GetMapping("/{id}/price")
    public Map<String, Object> getProductPrice(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        double price = productService.getProductPriceById(id);
        if (price >= 0) {
            response.put("success", true);
            response.put("price", price);
        } else {
            response.put("success", false);
            response.put("message", "Sản phẩm không tồn tại!");
        }
        return response;
    }

    // 3. Thêm mới sản phẩm (Đã bổ sung @Valid)
    // POST: http://localhost:8080/api/products
    @PostMapping
    public Map<String, Object> addProduct(@Valid @RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        boolean success = productService.addProduct(
                product.getProductName(),
                product.getBrand(),
                product.getPrice(),
                product.getStock()
        );

        response.put("success", success);
        response.put("message", success ? "Thêm sản phẩm thành công!" : "Thêm sản phẩm thất bại!");
        return response;
    }

    // 4. Cập nhật sản phẩm theo ID (Đã bổ sung @Valid)
    // PUT: http://localhost:8080/api/products/1
    @PutMapping("/{id}")
    public Map<String, Object> updateProduct(@PathVariable int id, @Valid @RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        boolean success = productService.updateProduct(
                id,
                product.getProductName(),
                product.getBrand(),
                product.getPrice(),
                product.getStock()
        );

        response.put("success", success);
        response.put("message", success ? "Cập nhật sản phẩm thành công!" : "Cập nhật sản phẩm thất bại!");
        return response;
    }

    // 5. Xóa sản phẩm theo ID
    // DELETE: http://localhost:8080/api/products/1
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteProduct(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = productService.deleteProduct(id);
        response.put("success", success);
        response.put("message", success ? "Xóa sản phẩm thành công!" : "Xóa thất bại!");
        return response;
    }
}