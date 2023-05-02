package com.rusozx.coffeManagment.serviceImpl;

import com.google.common.base.Strings;
import com.rusozx.coffeManagment.JWT.CustomerUserDetailsService;
import com.rusozx.coffeManagment.JWT.JwtFilter;
import com.rusozx.coffeManagment.JWT.JwtUtil;
import com.rusozx.coffeManagment.entity.User;
import com.rusozx.coffeManagment.constants.CoffeConstants;
import com.rusozx.coffeManagment.dao.UserDao;
import com.rusozx.coffeManagment.service.UserService;
import com.rusozx.coffeManagment.utils.CoffeUtils;
import com.rusozx.coffeManagment.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Sign up:", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return CoffeUtils.getResponseEntity("Successfully", HttpStatus.OK);
                } else
                    return CoffeUtils.getResponseEntity("Email Already Exists", HttpStatus.BAD_REQUEST);
            } else
                return CoffeUtils.getResponseEntity(CoffeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }


    private boolean validateSignUpMap(Map<String,String> requestMap){
         return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") &&
                 requestMap.containsKey("email") && requestMap.containsKey("pwd");
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPwd(requestMap.get("pwd"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> logIn(Map<String, String> requestMap) {

        try{

            Authentication auth= authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("pwd"))
            );
            if(auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true"))
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole()) + "\"}"
                            , HttpStatus.OK);
                else
                    return new ResponseEntity<String>("{\"message\":\" Wait for Admin Aproval.\"}",
                            HttpStatus.OK);
            }
            return new ResponseEntity<String>("{\"message\":\" Bad Credentials.\"}",
                    HttpStatus.BAD_REQUEST);
        }catch(Exception ex){
            log.error("{}",ex);
        }
        return new ResponseEntity<String>("{\"message\":\" Bad Credentials.\"}",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<User>> getAllUsers() {
        try{
            if(jwtFilter.isAdmin())
                return new ResponseEntity<List<User>>(userDao.getAllUsersByRole("user"), HttpStatus.OK);
            else
                return new ResponseEntity<List<User>>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<User>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                Optional<User> optional =userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(optional.isPresent()) {
                    if(Strings.isNullOrEmpty(requestMap.get("status"))) {
                        userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                        sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                        return CoffeUtils.getResponseEntity("User status Updated Successfully", HttpStatus.OK);
                    }
                    return CoffeUtils.getResponseEntity(CoffeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
                else
                    return CoffeUtils.getResponseEntity("User id does not exist", HttpStatus.OK);
            }else{
                return CoffeUtils.getResponseEntity(CoffeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }


    private void sendMailToAllAdmin(String status, String userEmail, List<String> allAdmin){
        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status!=null && status.equalsIgnoreCase("true"))
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Aproved","User: "+userEmail+
                    " is aproved by "+jwtFilter.getCurrentUser(),allAdmin);
        else
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled","User: "+userEmail+
                    " is aproved by "+jwtFilter.getCurrentUser(),allAdmin);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CoffeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePwd(Map<String, String> requestMap) {
        try{
            User user = userDao.findByEmail(jwtFilter.getCurrentUser());
            if(!Objects.isNull(user)) {
                if (user.getPwd().equalsIgnoreCase(requestMap.get("oldPwd"))) {
                    user.setPwd(requestMap.get("newPwd"));
                    userDao.save(user);
                    return CoffeUtils.getResponseEntity("Password updated",
                            HttpStatus.OK);
                }
                return CoffeUtils.getResponseEntity("Incorrect old password",
                        HttpStatus.BAD_REQUEST);
            }
            return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPwd(Map<String, String> requestMap) {
        try{
            User user = userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())){
                emailUtils.forgotMail(user.getEmail(), "Forgot Coffee Password", user.getPwd());
                return CoffeUtils.getResponseEntity("Email sent",HttpStatus.OK);
            }
            return CoffeUtils.getResponseEntity("Your email is incorrect",HttpStatus.BAD_REQUEST);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
