package com.rusozx.coffeManagment.service;


import com.rusozx.coffeManagment.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<String> signUp(Map<String, String> requestMap);
    ResponseEntity<String> logIn(Map<String, String> requestMap);
    ResponseEntity<List<User>> getAllUsers();
    ResponseEntity<String> update(Map<String, String> requestMap);
    ResponseEntity<String> checkToken();
    ResponseEntity<String> changePwd(Map<String, String> requestMap);
    ResponseEntity<String> forgotPwd(Map<String, String> requestMap);
}
