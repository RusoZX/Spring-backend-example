package com.rusozx.coffeManagment.dao;

import com.rusozx.coffeManagment.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserDaoTest {
    @Autowired
    UserDao underTest;
    @Test
    void findByEmail() {
        //given
        String email="zverevdaniil103@gmail.com";
        //Expected
        User expected = new User();
        expected.setId(3);
        expected.setContactNumber("1234");
        expected.setEmail(email);
        expected.setName("user");
        expected.setPwd("user1");
        expected.setRole("user");
        expected.setStatus("true");

        //then
        User actualResponse = underTest.findByEmail(email);
        //asert
        assertEquals(expected, actualResponse);
    }

    @Test
    void getAllUsersByRole() {
        //given
        String role="admin";
        //Expected
        List<User> expected= new ArrayList<>();
        User user = new User();
        user.setId(2);
        user.setContactNumber("1234");
        user.setEmail("rusozx76@gmail.com");
        user.setName("admin");
        user.setPwd("admin");
        user.setRole(role);
        user.setStatus("true");
        expected.add(user);

        //then
        List<User> actualResponse = underTest.getAllUsersByRole(role);
        //asert
        assertEquals(expected, actualResponse);
    }

    @Test
    void getAllAdmin() {
        //Expected
        List<String> expected= new ArrayList<>();
        expected.add("rusozx76@gmail.com");
        //then
        List<String> actualResponse = underTest.getAllAdmin();
        //asert
        assertEquals(expected, actualResponse);
    }

    @Test
    void updateStatus() {
        //Do the update
        Integer id = 1;
        String newStatus="false";
        underTest.updateStatus(newStatus,id);
        //Expected
        User expected = new User();
        expected.setId(1);
        expected.setContactNumber("123456789");
        expected.setEmail("abueli@gmail.com");
        expected.setName("abueli");
        expected.setPwd("123456");
        expected.setRole("user");
        expected.setStatus("false");

        //then
        User actualResponse = underTest.findByEmail("abueli@gmail.com");
        //asert
        assertEquals(expected, actualResponse);
        underTest.updateStatus("true",id);
    }
}