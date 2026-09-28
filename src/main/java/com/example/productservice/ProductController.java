package com.example.productservice;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final List<Product> products = new ArrayList<>(List.of(
            new Product(1L, "Laptop", 999.99),
            new Product(2L, "Mouse", 25.50),
            new Product(3L, "Bottle", 65.50)
    ));

    // GET http://localhost:8080/api/products
    @GetMapping
    public List<Product> getAllProducts() {
        return products; // Spring automatically converts this List to a JSON array
    }

    // GET http://localhost:8080/api/products/1
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // POST http://localhost:8080/api/products
    @PostMapping
    public Product createProduct(@RequestBody Product newProduct) {
        products.add(newProduct);
        return newProduct;
    }
}