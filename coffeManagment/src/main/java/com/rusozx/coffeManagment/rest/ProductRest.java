package com.rusozx.coffeManagment.rest;

import com.rusozx.coffeManagment.entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path="/product")
public interface ProductRest {

    @PostMapping(path="/add")
    ResponseEntity<String> addNewProduct(@RequestBody Map<String, String> requestMap);

    @GetMapping(path="/get")
    ResponseEntity<List<Product>> getAllProduct();

    @PostMapping(path="/update")
    ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap);

    @PostMapping(path="/delete/{id}")
    ResponseEntity<String> deleteProduct(@RequestParam Integer id);

    @PostMapping(path="/updateStatus")
    ResponseEntity<String> updateStatus(@RequestBody Map<String, String> requestMap);

    @GetMapping(path="/getByCategory/{id}")
    ResponseEntity<List<Product>> getAllProductByCategory(@PathVariable Integer id);

    @GetMapping(path="/getById/{id}")
    ResponseEntity<Product> getProductById(@RequestParam Integer id);
}
