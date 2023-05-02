package com.rusozx.coffeManagment.rest;



import com.rusozx.coffeManagment.entity.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path="/category")
public interface CategoryRest {

    @PostMapping(path="/add")
    ResponseEntity<String> addNewCategory(@RequestBody Map<String, String> requestMap);

    @GetMapping(path="/get")
    ResponseEntity<List<Category>> getAllCategory();

    @PostMapping(path="/update")
    ResponseEntity<String> updateCategory(@RequestBody Map<String, String> requestMap);
}
