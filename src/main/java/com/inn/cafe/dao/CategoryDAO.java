package com.inn.cafe.dao;

import com.inn.cafe.pojo.Category;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Integer> {

    List<Category> findAll();
    Optional<Category> findByName(String name);

    List<Category> getAllCategories(String filterValue);

}
