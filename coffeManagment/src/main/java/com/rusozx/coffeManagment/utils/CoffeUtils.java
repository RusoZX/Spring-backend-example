package com.rusozx.coffeManagment.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;


public class CoffeUtils {
    private CoffeUtils(){

    }
    public static ResponseEntity<String> getResponseEntity(String msg, HttpStatus status){
        return new ResponseEntity<String>("{\"message\":\""+msg+"\"}", status);
    }
    public static String getUUID(){
        Date date = new Date();
        long time = date.getTime();
        return "BILL-"+time;
    }

}
