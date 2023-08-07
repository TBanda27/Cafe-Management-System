package com.inn.cafe.pojo;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@NamedQuery(name = "Category.getAllCategories", query = "SELECT c FROM Category c WHERE c.id in (SELECT p.category from Product p WHERE p.status = true)")
@Entity
@Data
@Table(name = "categories")
public class Category implements Serializable {

    private static final Long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;


    @Column(name = "name", unique = true)
    private String name;

//    @OneToMany(mappedBy = "category")
//    private Set<Product> products;

}
