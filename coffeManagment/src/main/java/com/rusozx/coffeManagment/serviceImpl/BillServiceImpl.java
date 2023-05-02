package com.rusozx.coffeManagment.serviceImpl;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rusozx.coffeManagment.JWT.JwtFilter;
import com.rusozx.coffeManagment.entity.Bill;
import com.rusozx.coffeManagment.entity.User;
import com.rusozx.coffeManagment.constants.CoffeConstants;
import com.rusozx.coffeManagment.dao.BillDao;
import com.rusozx.coffeManagment.dao.UserDao;
import com.rusozx.coffeManagment.service.BillService;
import com.rusozx.coffeManagment.utils.CoffeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService {
    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BillDao billDao;

    @Autowired
    UserDao userDao;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try{
            String fileName;
            if(validateRequest(requestMap)) {
                if (requestMap.containsKey("isGenerate") && requestMap.get("isGenerate").equals("true"))
                    fileName = String.valueOf(requestMap.get("uuid"));
                else
                    fileName = CoffeUtils.getUUID();

                requestMap.put("uuid", fileName);
                insertBill(requestMap);
                createPdf(requestMap, fileName);
                return new ResponseEntity<>("{\"uuid\":"+fileName+"\"",HttpStatus.OK);
            }
            return CoffeUtils.getResponseEntity(CoffeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private boolean validateRequest(Map<String, Object> map){
        return map.containsKey("name") && map.containsKey("contactNumber") && map.containsKey("email") &&
                map.containsKey("paymentMethod") && map.containsKey("productDetails") && map.containsKey("totalAmount");
    }
    private void insertBill(Map<String, Object> map){
        try{
            Bill bill = new Bill();
            bill.setUuid(String.valueOf(map.get("uuid")));
            bill.setName(String.valueOf(map.get("name")));
            bill.setEmail(String.valueOf(map.get("email")));
            bill.setContactNumber(String.valueOf(map.get("contactNumber")));
            bill.setPaymentMethod(String.valueOf(map.get("paymentMethod")));
            bill.setTotal(Integer.parseInt(String.valueOf(map.get("totalAmount"))));
            bill.setProductDetails(String.valueOf(map.get("productDetails")));
            bill.setCreatedBy(jwtFilter.getCurrentUser());

            billDao.save(bill);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private Font getFont(String type){
        Font font;
        switch (type){
            case "Header":
                font = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,10,BaseColor.BLACK);
                font.setStyle(Font.BOLD);
                break;
            case "Data":
                font = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                font.setStyle(Font.BOLD);
                break;
            default:
                font = new Font();
        }
        return font;
    }
    private Rectangle getRectangle(){
        Rectangle rect = new Rectangle(557, 825, 18, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderWidth(1);
        return rect;
    }
    private PdfPTable createTable(String data) throws JSONException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        Stream.of("Name", "Category","Quantity","Price", "Subtotal").forEach(columnTitle ->{
                    PdfPCell cell = new PdfPCell();
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setBorderWidth(2);
                    cell.setBackgroundColor(BaseColor.PINK);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setPhrase(new Phrase(columnTitle));
                    table.addCell(cell);
                });

        if(Strings.isNullOrEmpty(data)){

        }else{
            JSONArray jsonArray= new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++){
                Map<String, Object> dataMap= new Gson().fromJson(jsonArray.getString(i),
                        new TypeToken<Map<String, Object>>(){}.getType());
                addRows(table, dataMap);
            }
        }

        return table;
    }
    private void addRows(PdfPTable table, Map<String, Object> map){
        table.addCell(String.valueOf(map.get("name")));
        table.addCell(String.valueOf(map.get("category")));
        table.addCell(String.valueOf(map.get("quantity")));
        table.addCell(String.valueOf(map.get("price")));
        table.addCell(String.valueOf(map.get("total")));
    }
    private void createPdf(Map<String, Object> map, String fileName) throws FileNotFoundException, DocumentException,JSONException {
        String data = "Name: "+ map.get("name")+"\nEmail: "+ map.get("email")+
                "\nContact Number: "+ map.get("contactNumber")+"\nPayment Method: "+ map.get("paymentMethod")+"\n \n";
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(CoffeConstants.BILL_LOCATION+"\\"+fileName+".pdf"));
        doc.open();
        doc.add(getRectangle());

        Paragraph chunk = new Paragraph("COFFEE MS",getFont("Header"));
        chunk.setAlignment(Element.ALIGN_CENTER);
        doc.add(chunk);

        doc.add(new Paragraph(data,getFont("data")));
        doc.add(createTable(String.valueOf(map.get("productDetails"))));
        doc.add(new Paragraph("Total: "+map.get("totalAmount")+"\n Thank you",getFont("data")));
        doc.close();
    }


    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try{
            if(jwtFilter.isAdmin()){
                List<Bill> result = billDao.findAll();
                if(!result.isEmpty())
                    return new ResponseEntity<>(result, HttpStatus.OK);
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }else{
                User user = userDao.findByEmail(jwtFilter.getCurrentUser());
                if(!Objects.isNull(user)){
                    List<Bill> result = billDao.findAllByCreatedBy(user.getEmail());
                    if(!result.isEmpty())
                        return new ResponseEntity<>(result, HttpStatus.OK);
                    return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
                }
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<Bill> getBillById(Integer id) {
        try{
            Optional<Bill> bill = billDao.findById(id);
            if(bill.isPresent())
                return new ResponseEntity<>(bill.get(), HttpStatus.OK);
            return new ResponseEntity<>(new Bill(), HttpStatus.OK);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new Bill(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try{
            if(requestMap.containsKey("uuid")){
                String filePath = CoffeConstants.BILL_LOCATION+"\\"+String.valueOf(requestMap.get("uuid"))+".pdf";
                File file= new File(filePath);
                if(file.exists())
                    return new ResponseEntity<>(getByteArray(file), HttpStatus.OK);
                else{
                    requestMap.put("isGenerate", false);
                    generateReport(requestMap);
                    return new ResponseEntity<>(getByteArray(file), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(new byte[0], HttpStatus.BAD_REQUEST);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] getByteArray(File file) throws Exception{
        InputStream targetStream = new FileInputStream(file);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<Bill> bill = billDao.findById(id);
                if(bill.isPresent()){
                    billDao.delete(bill.get());
                    return CoffeUtils.getResponseEntity("Bill deleted successfully", HttpStatus.BAD_REQUEST);
                }
                return CoffeUtils.getResponseEntity(CoffeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            return CoffeUtils.getResponseEntity(CoffeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception ex){
            ex.printStackTrace();
        }
            return CoffeUtils.getResponseEntity(CoffeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

}
