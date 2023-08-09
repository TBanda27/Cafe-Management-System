package com.inn.cafe.rest.interfaces;


import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping("/add")
    ResponseEntity<String> addNewProduct(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/get")
    ResponseEntity<List<ProductWrapper>> getAllProducts();

    @PostMapping(path = "/update")
    ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap);

    @DeleteMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable("id") Integer productId);

    @PostMapping(path = "/updateStatus")
    ResponseEntity<String> updateProductStatus(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/getByCategory/{id}")
    ResponseEntity<List<ProductWrapper>> getProductsByCategory(@PathVariable("id") Integer id);

    @GetMapping(path = "/getByProduct/{id}")
    ResponseEntity<ProductWrapper> getProductById(@PathVariable("id") Integer id);
}
