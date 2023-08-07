package com.inn.cafe.service.implementations;

import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.CategoryDAO;
import com.inn.cafe.dao.ProductDAO;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.pojo.Category;
import com.inn.cafe.pojo.Product;
import com.inn.cafe.service.interfaces.ProductService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    ProductDAO productDAO;

    @Autowired
    CategoryDAO categoryDAO;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, false))
                {
                    Optional<Product> productOptional = productDAO.findByName(requestMap.get("name"));
                    if(productOptional.isEmpty()){
                        productDAO.save(getProductFromMap(requestMap, false));
                        return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_SUCCESSFULLY_ADDED, HttpStatus.OK);
                    }
                    else return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                }
                else return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try{
           return new ResponseEntity<>(productDAO.getAllProducts(), HttpStatus.OK);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, true)){
                    Optional<Product> productOptional = productDAO.findById(Integer.parseInt(requestMap.get("id")));
                    if(productOptional.isPresent()){
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus(productOptional.get().status);
                        productDAO.save(product);
                        return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_SUCCESSFULLY_UPDATED, HttpStatus.OK);
                    }
                    else return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_DOES_NOT_EXIST, HttpStatus.OK);
                }
                else return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer productId) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<Product> productOptional = productDAO.findById(productId);
                if(productOptional.isPresent()){
                    productDAO.deleteById(productId);
                    return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_SUCCESSFULLY_DELETED, HttpStatus.OK);
                }
                else return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_DOES_NOT_EXIST, HttpStatus.BAD_REQUEST);
            }
            else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProductStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin())
            {
                Optional<Product> productOptional = productDAO.findById(Integer.parseInt(requestMap.get("id")));
                if(productOptional.isPresent()){
                    productDAO.updateProductStatus(Boolean.parseBoolean(requestMap.get("status")), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_SUCCESSFULLY_UPDATED, HttpStatus.OK);
                }
                else return CafeUtils.getResponseEntity(CafeConstants.PRODUCT_DOES_NOT_EXIST, HttpStatus.BAD_REQUEST);
            }
            else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
       }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getProductsByCategory(Integer id) {
        try{

           return new ResponseEntity<>(productDAO.getProductsByCategory(id), HttpStatus.OK);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) return true;
        }
        return false;
    }
    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        categoryDAO.findById(Integer.parseInt(requestMap.get("categoryId"))).orElseThrow(
                ()-> new UsernameNotFoundException(String.format("Category id: %s not found", requestMap.get("categoryId")))
        );
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = new Product();
        if(isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }
        else{
            product.setStatus(true);
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Double.parseDouble(requestMap.get("price")));
        return product;
    }
}
