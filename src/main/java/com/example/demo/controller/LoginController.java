package com.example.demo.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.model.UserPrincipal;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.demo.security.SecurityConstants.EXPIRATION_TIME;
import static com.example.demo.security.SecurityConstants.SECRET;

@RestController
@AllArgsConstructor
public class LoginController {

    BCryptPasswordEncoder bCryptPasswordEncoder;
    UserService userService;
    UserRepository userRepository;
    AccountRepository accountRepository;
    AuthenticationManager authenticationManager;

    @RequestMapping(path = "/user/create", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String login, @RequestParam String password, @RequestParam String role) {

        if(userRepository.findByLogin(login)!=null)
            return new ResponseEntity(null, HttpStatus.CONFLICT);

        userService.createUser(firstName, lastName, login, password, role);
        return new ResponseEntity(null, HttpStatus.OK);
    }

    @PostMapping("/signIn")
    public String signIn(@RequestBody JSONObject jsonRequest) {

        if(!jsonRequest.containsKey("login") || !jsonRequest.containsKey("password"))
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST).toString();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(jsonRequest.get("login"), jsonRequest.get("password")));

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET.getBytes()));
    }

}


