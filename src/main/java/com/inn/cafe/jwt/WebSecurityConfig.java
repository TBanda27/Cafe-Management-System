package com.inn.cafe.jwt;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    CustomUsersDetailsService customUsersDetailsService;

    public void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(customUsersDetailsService);
    }
}
