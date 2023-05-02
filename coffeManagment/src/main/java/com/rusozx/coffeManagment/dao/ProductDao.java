package com.rusozx.coffeManagment.dao;


import com.rusozx.coffeManagment.entity.Category;
import com.rusozx.coffeManagment.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
    List<Product> getAllProductByStatus(@Param("status") String status);
    @Transactional
    @Modifying
    void updateStatus(@Param("status") String status, @Param("id") Integer id);
    List<Product> getAllProductByCategory(Category category);
}
