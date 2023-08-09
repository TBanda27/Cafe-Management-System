package com.inn.cafe.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity
@Table(name = "bills")
public class Bill implements Serializable {

    private final long serialVersionUID = 1234567890L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "paymentNumber")
    private String paymentMethod;

    @Column(name = "total")
    private Double totalAmount;

    @Column(name = "productDetails", columnDefinition = "json")
    private String productDetails;

    @Column(name = "createdBy")
    private String createdBy;
}
