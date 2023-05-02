package com.rusozx.coffeManagment.rest;
import com.rusozx.coffeManagment.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping(path="/user")
public interface UserRest {

    @PostMapping(path="/signup")
    ResponseEntity<String> signUp(@RequestBody Map<String, String> requestMap);

    @PostMapping(path="/login")
    ResponseEntity<String> login(@RequestBody Map<String, String> requestMap);

    @GetMapping(path="/get")
    ResponseEntity<List<User>> getAllUsers();

    @PostMapping(path="/update")
    ResponseEntity<String> update(@RequestBody Map<String, String> requestMap);

    @GetMapping(path="/checkToken")
    ResponseEntity<String> checkToken();

    @PostMapping(path="/changePwd")
    ResponseEntity<String> changePwd(@RequestBody Map<String, String> requestMap);

    @PostMapping(path="/forgotPwd")
    ResponseEntity<String> forgotPwd(@RequestBody Map<String, String> requestMap);

}
