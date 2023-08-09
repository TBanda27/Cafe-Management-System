package com.inn.cafe.rest.interfaces;

import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/user")
public interface UserRestInterface {

    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader);

    @GetMapping(path="/get")
    public ResponseEntity<List<UserWrapper>> getAllUsers();

    @PostMapping(path = "/activate")
    public ResponseEntity<String> activateUser(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/checkToken/{token}")
    ResponseEntity<String> checkToken(@RequestBody @PathVariable("token") String token);

    @PostMapping(path = "/changePassword")
    ResponseEntity<String> changePassword(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/forgotPassword")
    ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/forgotPasswordUpdate")
    ResponseEntity<String> forgotPasswordUpdate(@RequestBody Map<String, String> requestMap);

}
