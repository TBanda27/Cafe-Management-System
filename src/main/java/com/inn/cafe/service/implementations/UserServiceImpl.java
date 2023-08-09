package com.inn.cafe.service.implementations;

import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.jwt.CustomUsersDetailsService;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.jwt.JwtUtils;
import com.inn.cafe.pojo.User;
import com.inn.cafe.service.interfaces.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    CustomUsersDetailsService customUsersDetailsService;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    UserDao userDao;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) throws UsernameNotFoundException{
        log.info("Inside signup {}: ");
        try{
            if(validateSignup(requestMap)){
                Optional<User> optionalUser = userDao.findByEmail(requestMap.get("email"));
                if(optionalUser.isEmpty()){
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity(CafeConstants.USER_SUCCESSFULLY_REGISTERED, HttpStatus.OK);
                }
                return new ResponseEntity<>(String.format(CafeConstants.EMAIL_ALREADY_EXISTS, requestMap.get("email")), HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public boolean validateSignup(Map<String, String> requestMap){
//        if the signup form doesn't have enough data return false
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")&&
                requestMap.containsKey("email") && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(bCryptPasswordEncoder.encode(requestMap.get("password")));
        user.setRole("user");
        user.setStatus(Boolean.parseBoolean("false"));

        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if(authentication.isAuthenticated()){
                if(customUsersDetailsService.getUserDetails().isStatus()){
                    return new ResponseEntity<String>("{\"token\":\"" + jwtUtils.generateToken(
                            customUsersDetailsService.getUserDetails().getEmail(),
                            customUsersDetailsService.getUserDetails().getRole()
                    ) + "\"}",
                    HttpStatus.OK);
                }
                else{
                  return  CafeUtils.getResponseEntity(CafeConstants.WAIT_FOR_ADMIN_APPROVAL, HttpStatus.OK);
                }
            }

        }catch(Exception exception){
            log.error("{}", exception);
        }
        return  CafeUtils.getResponseEntity(CafeConstants.BAD_CREDENTIALS, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        log.info("Inside getAllUsers()");
        System.out.println("Inside get all users()");
        try{
            if(jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String> activateUser(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<User> optionalUser = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(optionalUser.isPresent()){
                    userDao.updateStatus(Boolean.parseBoolean(requestMap.get("status")), Integer.valueOf(requestMap.get("id")));
                    sendMailToAllAdmins(requestMap.get("status"), optionalUser.get().getEmail(), userDao.findAllAdmins());

                    return new ResponseEntity<>(CafeConstants.USER_SUCCESSFULLY_UPDATED, HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>(
                            String.format(CafeConstants.ID_DOES_NOT_EXIST, requestMap.get("id")), HttpStatus.BAD_REQUEST);
                }
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private void sendMailToAllAdmins(String status, String user, List<String> allAdmins) {
        allAdmins.remove(jwtFilter.getCurrentUser());
        if(status != null && Boolean.parseBoolean(status) == true ) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "User:- " + user + "\n has been approved by ADMIN-" + jwtFilter.getCurrentUser(), allAdmins);
            log.info("Email has been sent...");
        }
        else
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "User:- "+user + "\n has been approved by ADMIN-" + jwtFilter.getCurrentUser(), allAdmins );
    }
    @Override
    public ResponseEntity<String> checkToken(String token) {
        return new ResponseEntity<>("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User userObject = userDao.findByEmail(jwtFilter.getCurrentUser()).orElseThrow(
                    ()-> new UsernameNotFoundException(String.format(CafeConstants.EMAIL_NOT_FOUND, jwtFilter.getCurrentUser())));
            if(userObject.isStatus()) {
                if (bCryptPasswordEncoder.matches(requestMap.get("oldPassword"), userObject.getPassword())) {
                    if (!bCryptPasswordEncoder.matches(requestMap.get("newPassword"), userObject.getPassword())) {
                        userObject.setPassword(bCryptPasswordEncoder.encode(requestMap.get("newPassword")));
                        userDao.save(userObject);
                        return CafeUtils.getResponseEntity(CafeConstants.PASSWORD_SUCCESSFULLY_CHANGED, HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity(CafeConstants.NEW_PASSWORD_MATCHES_OLD, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INCORRECT_OLD_PASSWORD, HttpStatus.BAD_REQUEST);
                }
            }
            return CafeUtils.getResponseEntity(CafeConstants.USER_NOT_ACTIVATED, HttpStatus.BAD_REQUEST);

        }catch(Exception exception){
            exception.printStackTrace();
        }


        return null;
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try{
            userDao.findByEmail(requestMap.get("email")).orElseThrow(
                    ()-> new UsernameNotFoundException(String.format(CafeConstants.EMAIL_NOT_FOUND, requestMap.get("email"))));
            emailUtils.forgotPassword(requestMap.get("email"), "Forgot password for Cafe Management System");
            return CafeUtils.getResponseEntity(CafeConstants.CHECK_YOUR_EMAIL, HttpStatus.OK);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPasswordUpdate(Map<String, String> requestMap) {
        try{
            User user = userDao.findByEmail(requestMap.get("email")).orElseThrow(
                    ()-> new UsernameNotFoundException(String.format(CafeConstants.EMAIL_NOT_FOUND, requestMap.get("email"))));
            if(validateForgotPasswordAndUpdate(requestMap) ){
                if(user.isStatus()){
                    if(requestMap.get("password").equals(requestMap.get("confirmPassword"))){
                        user.setPassword(bCryptPasswordEncoder.encode(requestMap.get("password")));
                        userDao.save(user);
                        emailUtils.forgotPassword(requestMap.get("email"), "Password for Cafe Management System Changed");
                        return CafeUtils.getResponseEntity(CafeConstants.PASSWORD_SUCCESSFULLY_CHANGED, HttpStatus.OK);
                    }
                    else{
                        return CafeUtils.getResponseEntity(CafeConstants.PASSWORDS_DO_NOT_MATCH, HttpStatus.BAD_REQUEST);
                    }
                }
                else{
                    return CafeUtils.getResponseEntity(CafeConstants.USER_NOT_ACTIVATED, HttpStatus.BAD_REQUEST);
                }
            }
            else{
                return CafeUtils.getResponseEntity(CafeConstants.BAD_CREDENTIALS, HttpStatus.BAD_REQUEST);
            }

        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> logout(String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            jwtUtils.addToDenyList(token);
            return CafeUtils.getResponseEntity(CafeConstants.USER_SUCCESSFULLY_LOGGED_OUT, HttpStatus.OK);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public boolean validateForgotPasswordAndUpdate(Map<String, String> requestMap){
//        if the signup form doesn't have enough data return false
        return requestMap.containsKey("email")  && requestMap.containsKey("password") && requestMap.containsKey("confirmPassword");
    }
}
