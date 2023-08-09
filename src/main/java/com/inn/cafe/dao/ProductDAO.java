package com.inn.cafe.dao;

import com.inn.cafe.pojo.Product;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductDAO extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);
    List<ProductWrapper> getAllProducts();

    @Modifying
    @Transactional
    Integer updateProductStatus(boolean status, int id);

    List<ProductWrapper> getProductsByCategory(Integer id);

    ProductWrapper getProductById(Integer id);
}
