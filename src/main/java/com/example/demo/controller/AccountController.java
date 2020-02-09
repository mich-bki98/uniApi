package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.model.UserPrincipal;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CurrentUserAtt;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class AccountController {

    UserService userService;
    UserRepository userRepository;
    AccountRepository accountRepository;
    AuthenticationManager authenticationManager;

    @Secured("ROLE_ADMIN")
    @PostMapping("/account")
    public void createAccount(@RequestBody JSONObject jsonObject) throws Exception {
        Account account = Account.builder()
                .user(userRepository.findById(Long.valueOf(jsonObject.get("userId").toString())).orElseThrow(()->new Exception()))
                .eur(new BigDecimal(0))
                .pln(new BigDecimal(0))
                .pounds(new BigDecimal(0))
                .build();
        accountRepository.saveAndFlush(account);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/account/get")
    public List<JSONObject> getAccount(@CurrentUserAtt UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        List<JSONObject> accList = new ArrayList<>();
        for (Account account : user.getAccountList()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",account.getId());
            jsonObject.put("eur",account.getEur());
            jsonObject.put("pln",account.getPln());
            jsonObject.put("pounds",account.getPounds());
            accList.add(jsonObject);
        }
        return accList;
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/account/delete")
    public void deleteAccount(@RequestBody JSONObject jsonObject){
        Long id = (Long) jsonObject.get("id");
        accountRepository.delete(accountRepository.getOne(id));
        accountRepository.flush();
    }

}
