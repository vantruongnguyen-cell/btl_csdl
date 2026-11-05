package service;

import dao.ProductDAO;
import model.Product;

import java.util.List;

public class ProductService {
    private ProductDAO productDAO = new ProductDAO();

    public boolean existsById(int id) {
        return productDAO.getProductById(id) != null;
    }

    public boolean existsByBrand(String brand) {
        List<Product> products = productDAO.searchByBrand(brand);
        return products != null && !products.isEmpty();
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> getProductsByBrand(String brand) {
        return productDAO.searchByBrand(brand);
    }

    // ==================== CÁC THAO TÁC XỬ LÝ NGHIỆP VỤ ====================

    public boolean addProduct(String name, String brand, double price, int stock) {
        if (price <= 0 || stock < 0) {
            System.out.println("Lỗi: Giá bán phải lớn hơn 0 và số lượng không được âm.");
            return false;
        }
        Product p = new Product(name, brand, price, stock);
        boolean success = productDAO.addProduct(p);
        if (success) {
            System.out.println("Thêm sản phẩm thành công!");
        } else {
            System.out.println("Thêm sản phẩm thất bại!");
        }
        return success;
    }

    public boolean updateProduct(int id, String name, String brand, double price, int stock) {
        if (price <= 0 || stock < 0) {
            System.out.println("Lỗi: Giá bán phải lớn hơn 0 và số lượng không được âm.");
            return false;
        }
        Product p = new Product(id, name, brand, price, stock);
        boolean success = productDAO.updateProduct(p);
        if (success) {
            System.out.println("Cập nhật sản phẩm thành công!");
        } else {
            System.out.println("Cập nhật sản phẩm thất bại!");
        }
        return success;
    }

    public boolean deleteProduct(int id) {
        boolean success = productDAO.deleteProduct(id);
        if (success) {
            System.out.println("Xóa sản phẩm thành công!");
        } else {
            System.out.println("Xóa sản phẩm thất bại!");
        }
        return success;
    }

    public double getProductPriceById(int productId) {
        return productDAO.getProductPriceById(productId);
    }

    public void displayAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Danh sách sản phẩm trống.");
            return;
        }
        System.out.println("\n-------------------------------- DANH SÁCH SẢN PHẨM --------------------------------");
        System.out.printf("%-5s | %-30s | %-15s | %-15s | %-10s\n", "ID", "Tên sản phẩm", "Hãng", "Giá", "Tồn kho");
        System.out.println("------------------------------------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-5d | %-30s | %-15s | %-15.2f | %-10d\n",
                    p.getProductId(), p.getProductName(), p.getBrand(), p.getPrice(), p.getStock());
        }
    }

    public void searchByBrand(String brand) {
        List<Product> products = productDAO.searchByBrand(brand);
        if (products.isEmpty()) {
            System.out.println("Không tìm thấy sản phẩm thuộc hãng: " + brand);
            return;
        }
        System.out.println("\n-------------------------------- KẾT QUẢ TÌM KIẾM --------------------------------");
        System.out.printf("%-5s | %-30s | %-15s | %-15s | %-10s\n", "ID", "Tên sản phẩm", "Hãng", "Giá", "Tồn kho");
        System.out.println("------------------------------------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-5d | %-30s | %-15s | %-15.2f | %-10d\n",
                    p.getProductId(), p.getProductName(), p.getBrand(), p.getPrice(), p.getStock());
        }
    }
}