package com.inn.cafe.service.implementations;

import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.BillDAO;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.pojo.Bill;
import com.inn.cafe.service.interfaces.BillService;
import com.inn.cafe.utils.CafeUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.Document;
import java.io.FileOutputStream;
import java.util.Map;

@Slf4j
@Service
public class BillServiceImplementation implements BillService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BillDAO billDAO;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport");
        try{
            String filename;
            if(validateGenerateReport(requestMap)){
                if(requestMap.containsKey("isGenerate") && !(boolean)requestMap.get("isGenerate")){
                    filename = (String) requestMap.get("uuid");
                }
                else{
                    filename = CafeUtils.getUUID();
                    requestMap.put("uuid", filename);
                    insertBill(requestMap);
                }
                String data = "Name : "+ requestMap.get("name") +"\n" +
                        "Contact Number: " + requestMap.get("contactNumber") + "\n" +
                        "Email: " + requestMap.get("email") + "\n" +
                        "Payment Method: " + requestMap.get("paymentMethod");

                Document document = (Document) new com.itextpdf.text.Document();
                PdfWriter.getInstance((com.itextpdf.text.Document) document, new FileOutputStream(CafeConstants.FILE_STORAGE_LOCATION + "\\" + filename + ".pdf"));
                ((com.itextpdf.text.Document) document).open();
                setRectangeInPdf((com.itextpdf.text.Document) document);

            }
            else return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.PARTIAL_CONTENT);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void setRectangeInPdf(com.itextpdf.text.Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rectange = new Rectangle(577, 825, 18, 15);
        rectange.enableBorderSide(1);
        rectange.enableBorderSide(2);
        rectange.enableBorderSide(4);
        rectange.enableBorderSide(8);

        rectange.setBackgroundColor(BaseColor.BLACK);
        rectange.setBorderWidth(1);
        document.add(rectange);
    }

    private void insertBill(Map<String, Object> requestMap) {

        try{
            Bill bill = new Bill();
            bill.setId((Integer)requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("name"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotalAmount(Double.parseDouble((String)requestMap.get("totalAmount")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDAO.save(bill);

        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    private boolean validateGenerateReport(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }
}
