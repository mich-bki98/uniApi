package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(String firstName, String lastName, String login, String password){
        List<Account> accountList = new ArrayList<>();
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .login(login)
                .password(password)
                .accountList(accountList)
                .build();
        userRepository.save(user);
    }

}
