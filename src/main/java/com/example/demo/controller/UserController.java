package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.model.UserPrincipal;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CurrentUserAtt;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.bcel.BcelAccessForInlineMunger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;

    @Secured("ROLE_ADMIN")
    @RequestMapping(path = "/user/users", method = RequestMethod.GET)
    public List<JSONObject> usersResponse() {
        List<JSONObject> responses = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            JSONObject response = new JSONObject();
            response.put("id",user.getId());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("login", user.getLogin());
            response.put("password", user.getPassword());
            responses.add(response);
        }
        return responses;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/user/getUser")
    public JSONObject getUser(@RequestBody JSONObject jsonObject) throws Exception {
        JSONObject response = new JSONObject();
        User user = userRepository.getOne((long)Integer.parseInt(jsonObject.get("userId").toString()));
        if (user == null){
            throw new Exception();
        }
        response.put("id",user.getId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("login", user.getLogin());
        response.put("password", user.getPassword());
        return response;
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @PutMapping("/user")
    public ResponseEntity updateUser(@RequestBody JSONObject jsonObject, @CurrentUserAtt UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        if(!jsonObject.containsKey("field") || !jsonObject.containsKey("value"))
            return new ResponseEntity("Bad request!!!", HttpStatus.BAD_REQUEST);
        String field = (String) jsonObject.get("field");
        String value = (String) jsonObject.get("value");
        switch (field) {
            case "login":
                user.setLogin(value);
                break;
            case "password":
                user.setPassword(bCryptPasswordEncoder.encode(value));
                break;
            case "firstName":
                user.setFirstName(value);
                break;
            case "lastName":
                user.setLastName(value);
                break;
        }
        userRepository.saveAndFlush(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/user/delete")
    public void deleteUser(@RequestBody JSONObject jsonObject){
        Long userId = Long.valueOf(jsonObject.get("id").toString());
        User user = userRepository.getOne(userId);
        List<Account> allByUserEquals = accountRepository.findAllByUserEquals(user);
        accountRepository.deleteAll(allByUserEquals);
        accountRepository.flush();
        userRepository.delete(user);
        userRepository.flush();
    }

}
