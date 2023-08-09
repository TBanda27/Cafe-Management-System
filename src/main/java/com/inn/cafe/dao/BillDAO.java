package com.inn.cafe.dao;

import com.inn.cafe.pojo.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillDAO extends JpaRepository<Bill, Integer> {

}
