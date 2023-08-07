package com.inn.cafe.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(name = "Product.getAllProducts", query = "SELECT new com.inn.cafe.wrapper.ProductWrapper(p.id, p.name, p.category.id, p.category.name, p.description, p.price, p.status) FROM Product p")
@NamedQuery(name = "Product.updateProductStatus", query = "UPDATE Product p SET p.status = :status WHERE p.id = :id")
@NamedQuery(name = "Product.getProductsByCategory", query = "SELECT new com.inn.cafe.wrapper.ProductWrapper(p.id, p.name, p.category.id, p.category.name, p.description, p.price, p.status) FROM Product p WHERE p.category.id = :id AND p.status = TRUE")

@Data
@Entity
@Table(name = "products")
public class Product implements Serializable {
    private static final Long serialVersionUID = 123456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "name")
    public String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk", nullable = false)
    public Category category;

    @Column(name = "description")
    public String description;

    @Column(name = "price")
    public Double price;

    @Column(name = "status")
    public boolean status;

}
