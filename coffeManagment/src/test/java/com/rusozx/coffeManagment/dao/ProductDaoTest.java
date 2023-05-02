package com.rusozx.coffeManagment.dao;

import com.rusozx.coffeManagment.entity.Category;
import com.rusozx.coffeManagment.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@SpringBootTest
class ProductDaoTest {

    @Autowired
    ProductDao underTest;

    @Test
    void getAllProductByStatus() {
        //given
        String status="true";
        //expected
        List<Product> expected = new ArrayList<>();
        Product product= new Product();
        product.setId(1);
        product.setDescription("its a potato");
        product.setName("potato");
        product.setPrice(4);
        product.setStatus("true");
        product.setCategory(new Category(2,"fo0d1"));
        expected.add(product);

        product= new Product();
        product.setId(3);
        product.setDescription("its a pizzo");
        product.setName("pizzo");
        product.setPrice(11);
        product.setStatus("true");
        product.setCategory(new Category(1,"food"));
        expected.add(product);

        product= new Product();
        product.setId(4);
        product.setDescription("its a pizzo");
        product.setName("pizza");
        product.setPrice(17);
        product.setStatus("true");
        product.setCategory(new Category(1,"food"));
        expected.add(product);

        //then
        List<Product> actualResult = underTest.getAllProductByStatus(status);
        //assert
        assertEquals(expected,actualResult);
    }

    @Test
    void updateStatus() {
        Integer productId = 3;
        String newStatus = "false";

        //update the status
        underTest.updateStatus(newStatus, productId);
        //Expected
        Product expected= new Product();
        expected.setId(3);
        expected.setDescription("its a pizzo");
        expected.setName("pizzo");
        expected.setPrice(11);
        expected.setStatus("false");
        expected.setCategory(new Category(1,"food"));

        //then
        Optional<Product> actualResult = underTest.findById(productId);
        assertTrue(actualResult.isPresent());
        assertEquals(expected,actualResult.get());
        underTest.updateStatus("true", productId);

    }

    @Test
    void getAllByCategory() {
        //given
        Integer category = 1;
        //expected
        List<Product> expected = new ArrayList<>();
        Product product= new Product();
        product.setId(3);
        product.setDescription("its a pizzo");
        product.setName("pizzo");
        product.setPrice(11);
        product.setStatus("true");
        product.setCategory(new Category(1,"food"));
        expected.add(product);

        product= new Product();
        product.setId(4);
        product.setDescription("its a pizzo");
        product.setName("pizza");
        product.setPrice(17);
        product.setStatus("true");
        product.setCategory(new Category(1,"food"));
        expected.add(product);
        System.out.println(expected);
        //then
        List<Product> actualResult = underTest.getAllProductByCategory(new Category(1,""));
        //assert
        assertEquals(actualResult,expected);
    }
}