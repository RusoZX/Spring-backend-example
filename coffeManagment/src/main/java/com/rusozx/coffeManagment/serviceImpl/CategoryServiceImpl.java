package com.rusozx.coffeManagment.serviceImpl;

import com.rusozx.coffeManagment.JWT.JwtFilter;
import com.rusozx.coffeManagment.entity.Category;
import com.rusozx.coffeManagment.constants.CoffeConstants;
import com.rusozx.coffeManagment.dao.CategoryDao;
import com.rusozx.coffeManagment.service.CategoryService;
import com.rusozx.coffeManagment.utils.CoffeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;


    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validate(requestMap, false)){
                    categoryDao.save(getCategoryFromMap(requestMap, false));

                    return CoffeUtils.getResponseEntity("Category created successfully", HttpStatus.OK);
                }
                return CoffeUtils.getResponseEntity(CoffeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else
                return CoffeUtils.getResponseEntity(CoffeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validate(Map<String, String> map, boolean validateId){
        return map.containsKey("name") && (map.containsKey("id") || !validateId);
    }
    private Category getCategoryFromMap(Map<String, String> map, boolean isAdd){
        Category category = new Category();
        if(isAdd)
            category.setId(Integer.parseInt(map.get("id")));
        category.setName(map.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filter) {
        try{
            if(filter.equalsIgnoreCase("true"))
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
            else
                return new ResponseEntity<List<Category>>(categoryDao.findAll(),HttpStatus.OK);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validate(requestMap, true)){
                    Optional<Category> optCategory = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if(optCategory.isPresent())
                        categoryDao.save(getCategoryFromMap(requestMap, true));
                    else
                        return CoffeUtils.getResponseEntity("Category id doesnt exist", HttpStatus.OK);

                    return CoffeUtils.getResponseEntity("Category updated successfully", HttpStatus.OK);
                }
                return CoffeUtils.getResponseEntity(CoffeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else
                return CoffeUtils.getResponseEntity(CoffeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
