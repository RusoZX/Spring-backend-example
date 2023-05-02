package com.rusozx.coffeManagment.dao;

import com.rusozx.coffeManagment.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillDao extends JpaRepository<Bill, Integer> {
    List<Bill> findAllByCreatedBy(String createdBy);
}
