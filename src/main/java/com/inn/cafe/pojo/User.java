package com.inn.cafe.pojo;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")
@NamedQuery(name = "User.getAllUsers", query = "SELECT new com.inn.cafe.wrapper.UserWrapper(u.id, u.name, u.contactNumber, u.email, u.status) FROM User u WHERE u.role = 'user'")
@NamedQuery(name = "User.findAllAdmins", query = "SELECT u.email FROM User u WHERE u.role = 'admin'")
//@NamedQuery(name = "User.updateStatus", query = "UPDATE User u SET u.status = CASE WHEN u.status = true THEN false ELSE true END WHERE u.id = :id")
@NamedQuery(name = "User.updateStatus", query = "UPDATE User u SET u.status = :status  WHERE u.id = :id")
@Entity
@Data
//@DynamicInsert  //
//@DynamicUpdate
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "status")
    private boolean status;
}
