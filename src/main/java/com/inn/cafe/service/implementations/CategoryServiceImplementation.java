package com.inn.cafe.service.implementations;


import com.google.common.base.Strings;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.CategoryDAO;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.pojo.Category;
import com.inn.cafe.service.interfaces.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryServiceImplementation implements CategoryService {

    @Autowired
    CategoryDAO categoryDAO;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, false)){
                    Optional<Category> categoryOptional = categoryDAO.findByName(requestMap.get("name"));
                    if(!categoryOptional.isPresent()){
                        categoryDAO.save(getCategoryFromMap(requestMap, false));
                        return CafeUtils.getResponseEntity(CafeConstants.CATEGORY_SUCCESSFULLY_SAVED, HttpStatus.OK);
                    }
                    else{
                        return CafeUtils.getResponseEntity(CafeConstants.CATEGORY_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                    }
                }
            }
            else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED );
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId){
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId){
                return true;
            }
        }
        return false;
    }

    public Category getCategoryFromMap(Map<String, String> requestMap, boolean isAdd){
        Category category = new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategories(String filterValue) {
        try{
            if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                return new ResponseEntity<>(categoryDAO.getAllCategories(filterValue), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDAO.findAll(), HttpStatus.OK);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR) ;
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {

        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, true)){
                    Optional<Category> optionalCategory = categoryDAO.findById(Integer.parseInt(requestMap.get("id")));
                    if(optionalCategory.isPresent()){
                        categoryDAO.save(getCategoryFromMap(requestMap, true));
                        return CafeUtils.getResponseEntity(CafeConstants.CATEGORY_SUCCESSFULLY_UPDATED, HttpStatus.OK);
                    }
                    else return CafeUtils.getResponseEntity(String.format(CafeConstants.CATEGORY_ID_DOESNT_EXIST, Integer.parseInt(requestMap.get("id"))),
                            HttpStatus.BAD_REQUEST);
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else  return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
