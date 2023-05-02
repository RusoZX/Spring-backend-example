package com.rusozx.coffeManagment.serviceImpl;

import com.rusozx.coffeManagment.JWT.JwtFilter;
import com.rusozx.coffeManagment.entity.Category;
import com.rusozx.coffeManagment.entity.Product;
import com.rusozx.coffeManagment.constants.CoffeConstants;
import com.rusozx.coffeManagment.dao.CategoryDao;
import com.rusozx.coffeManagment.dao.ProductDao;
import com.rusozx.coffeManagment.service.ProductService;
import com.rusozx.coffeManagment.utils.CoffeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validate(requestMap, false)){
                    productDao.save(getProductFromMap(requestMap, false));
                    return CoffeUtils.getResponseEntity("Product created successfully", HttpStatus.OK);
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
        return map.containsKey("name") && map.containsKey("categoryId") && map.containsKey("categoryName")
                && map.containsKey("description") && map.containsKey("price")
                && (map.containsKey("id") || !validateId);
    }
    private Product getProductFromMap(Map<String, String> map, boolean isAdd){
        Product product = new Product();

        Category category = new Category();
        category.setId(Integer.parseInt(map.get("categoryId")));
        category.setName(map.get("categoryName"));

        if(isAdd)
            product.setId(Integer.parseInt(map.get("id")));
        else
            product.setStatus("true");
        product.setCategory(category);
        product.setName(map.get("name"));
        product.setDescription(map.get("description"));
        product.setPrice(Integer.parseInt(map.get("price")));

        return product;
    }
    @Override
    public ResponseEntity<List<Product>> getAllProduct() {
        try{
            log.info("im here");
            return new ResponseEntity<List<Product>>(productDao.getAllProductByStatus("true"), HttpStatus.OK);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validate(requestMap, false)){
                    Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if(optional.isPresent()) {
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus(optional.get().getStatus());
                        productDao.save(product);
                        return CoffeUtils.getResponseEntity("Product updated successfully", HttpStatus.OK);
                    }
                    return CoffeUtils.getResponseEntity("Product Id does not exist", HttpStatus.BAD_REQUEST);
                }
                return CoffeUtils.getResponseEntity(CoffeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else
                return CoffeUtils.getResponseEntity(CoffeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{
            if(jwtFilter.isAdmin()) {
                Optional<Product> optional = productDao.findById(id);
                if (optional.isPresent()) {
                    productDao.delete(optional.get());
                    return CoffeUtils.getResponseEntity("Product successfully deleted", HttpStatus.OK);
                } else
                    return CoffeUtils.getResponseEntity("There is no product by that id", HttpStatus.OK);
            }
            return CoffeUtils.getResponseEntity(CoffeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()) {
                if(requestMap.containsKey("id")&&requestMap.containsKey("status")) {
                    Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (optional.isPresent()) {
                        productDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                        return CoffeUtils.getResponseEntity("Status of product successfully updated", HttpStatus.OK);
                    } else
                        return CoffeUtils.getResponseEntity("There is no product by that id", HttpStatus.OK);
                }
                return CoffeUtils.getResponseEntity(CoffeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            return CoffeUtils.getResponseEntity(CoffeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Product>> getAllProductByCategory(Integer id) {
        try{
            Optional<Category> optional = categoryDao.findById(id);
            if(optional.isPresent()){
                List<Product> result = productDao.getAllProductByCategory(new Category(id,""));
                if(!result.isEmpty())
                    return new ResponseEntity<>(result, HttpStatus.OK);
                else
                    return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Product> getProductById(Integer id) {
        try{
            Optional<Product> optional= productDao.findById(id);
            if(optional.isPresent()){
                return new ResponseEntity<>(optional.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Product(), HttpStatus.BAD_REQUEST);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new Product(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
