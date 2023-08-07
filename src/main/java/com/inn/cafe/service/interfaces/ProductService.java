package com.inn.cafe.service.interfaces;


import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getAllProducts();

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    ResponseEntity<String> deleteProduct(Integer productId);

    ResponseEntity<String> updateProductStatus(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getProductsByCategory(Integer categoryId);
}
