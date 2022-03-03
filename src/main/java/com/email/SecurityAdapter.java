package com.email;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
       httpSecurity.authorizeRequests(a ->
                       a.antMatchers("/","/error")
                       .permitAll().anyRequest().authenticated()).
               exceptionHandling(e->
                        e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
               .csrf(c->c.disable()).logout(
                        lg->lg.logoutSuccessUrl("/").permitAll())
               .oauth2Login();
    }

}
