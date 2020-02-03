package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.model.UserPrincipal;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.MultipliersRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CurrentUserAtt;
import lombok.AllArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class TransactionController {

    AccountRepository accountRepository;
    MultipliersRepository multipliersRepository;
    UserRepository userRepository;

    private ResponseEntity respond(String message) {
        return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    private void withdrawTransaction(JSONObject jsonObject, User user) {
        Account account = accountRepository.findAccountById(
                Integer.parseInt(jsonObject.get(jsonObject.get("accountId")).toString()));
        BigDecimal amount = BigDecimal.valueOf(Integer.parseInt(jsonObject.get("amount").toString()));
        if (user != account.getUser()) {
            respond("Account does not belong to User!");
        } else {
            switch (jsonObject.get("currency").toString()) {
                case "eur":
                    if(account.getEur().compareTo(amount) < 1){
                        respond("")
                    }
                    account.setEur(account.getEur().subtract(amount));
            }
        }
    }

    private void depositTransaction(JSONObject jsonObject, User user) {
    }

    private void sendTransaction(JSONObject jsonObject, User user) {
    }

    private void changeCurrencyTransaction(JSONObject jsonObject, User user) {
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PutMapping
    public ResponseEntity chooseTransaction(@RequestBody JSONObject jsonObject, @CurrentUserAtt UserPrincipal userPrincipal) {
        if (!jsonObject.containsKey("transactionType"))
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);

        User user = userPrincipal.getUser();
        switch (jsonObject.get("transactionType").toString()) {
            case "withdraw":
                if (!jsonObject.containsKey("currency") || !jsonObject.containsKey("amount") || !jsonObject.containsKey("accountId"))
                    break;
                withdrawTransaction(jsonObject, user);
                break;
            case "deposit":
                if (!jsonObject.containsKey("currency") || !jsonObject.containsKey("amount") || !jsonObject.containsKey("accountId"))
                    break;
                depositTransaction(jsonObject, user);
                break;
            case "sendCurrency":
                if (!jsonObject.containsKey("currency") || !jsonObject.containsKey("amount")
                        || !jsonObject.containsKey("recieverLogin") || !jsonObject.containsKey("accountId"))
                    break;
                sendTransaction(jsonObject, user);
                break;
            case "changeCurrency":
                if (!jsonObject.containsKey("currencyFrom") || !jsonObject.containsKey("currencyTo")
                        || !jsonObject.containsKey("amount") || !jsonObject.containsKey("accountId"))
                    break;
                changeCurrencyTransaction(jsonObject, user);
                break;
        }

        return new ResponseEntity(null, HttpStatus.OK);
    }
}
