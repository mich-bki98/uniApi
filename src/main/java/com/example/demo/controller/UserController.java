package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(name = "/user/create", method = RequestMethod.POST)
    public void createUser(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String login, @RequestParam String password) {
        userService.createUser(firstName, lastName, login, password);
    }

    @RequestMapping(name = "/user/users", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject usersResponse() {
        for (User user : userRepository.findAll()) {
            JSONObject response = new JSONObject();
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("login", user.getLogin());
            response.put("password", user.getPassword());
            return response;
        }
        return null;
    }

    @PostMapping("/user")
    public void singUp(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @RequestMapping(name = "/test", method = RequestMethod.DELETE)
    public JSONObject testS() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("answer", "works");
        return jsonObject;
    }
}
