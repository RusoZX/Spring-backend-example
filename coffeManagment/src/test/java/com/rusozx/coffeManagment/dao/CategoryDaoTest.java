package com.rusozx.coffeManagment.dao;

import com.rusozx.coffeManagment.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CategoryDaoTest {
    @Autowired
    CategoryDao underTest;

    @Test
    void getAllCategory() {
        //Expected
        List<Category> expected = List.of(
                new Category(2,"fo0d1"),
                new Category(1,"food")
                );
        //when
        List<Category> actualResult = underTest.getAllCategory();
        //assert
        assertEquals(actualResult,expected);
    }
}