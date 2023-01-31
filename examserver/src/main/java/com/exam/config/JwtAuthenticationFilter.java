package com.exam.config;

import com.exam.services.impl_classes.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//STEP 15 :  Filtering the data :::::::::::::::::: sent by user under Authorization tag of POSTMAN app  with username and password.

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;    //copied from google.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

       final String requestTokenHeader = request.getHeader("Authorization");
        System.out.println(requestTokenHeader);

        String username=null;
        String jwtToken=null;


if(requestTokenHeader !=null && requestTokenHeader.startsWith("Bearer ")) {
    //1. extract the token from header message , then find usename from the token.
    jwtToken = requestTokenHeader.substring(7);

    try {

        username = this.jwtUtil.extractUsername(jwtToken);

    } catch (ExpiredJwtException e) {
        e.printStackTrace();
        System.out.println("jwt token has expired ");
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("error");
    }
}
else {
    System.out.println("Invalid Token , not start with bearer String");
}

// 2.now token exits so validate it , then set it in securityContextHolder

//validating

        if(username!=null  && SecurityContextHolder.getContext().getAuthentication()==null)
        {
           final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

           if(this.jwtUtil.validateToken(jwtToken,userDetails))
           {
               //token is valid , set it in contextSecu....

               UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                 //set it in contextSecu....
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

           }
        }
        else
        {
            System.out.println("Token is not valid");
        }


// if validated the token ,  then chain it and forward it .

        filterChain.doFilter(request,response);





    }
}
