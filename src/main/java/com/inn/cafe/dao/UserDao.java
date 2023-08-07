package com.inn.cafe.dao;

//import com.inn.cafe.pojo.User;
import com.inn.cafe.pojo.User;
import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    List<UserWrapper> getAllUsers();

    @Transactional
    @Modifying
    Integer updateStatus(boolean status, Integer id);

    List<String> findAllAdmins();


}
