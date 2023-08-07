package com.inn.cafe.wrapper;

import com.inn.cafe.pojo.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWrapper {

    private Integer id;

    private String name;

    private Integer categoryId;

    private String categoryName;

    private String description;

    private Double price;

    private boolean status;



}
