package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Multipliers;
import com.example.demo.model.User;
import com.example.demo.model.UserPrincipal;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.MultipliersRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CurrentUserAtt;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@AllArgsConstructor
public class TransactionController {

    AccountRepository accountRepository;
    MultipliersRepository multipliersRepository;
    UserRepository userRepository;

    private ResponseEntity respond(String message) {
        return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity withdrawTransaction(JSONObject jsonObject, User user) {
        Account account = accountRepository.findAccountById(
                Integer.parseInt(jsonObject.get("accountId").toString()));
        BigDecimal amount = BigDecimal.valueOf(Integer.parseInt(jsonObject.get("amount").toString()));
        if (user != account.getUser()) {
            respond("Account does not belong to User!");
        } else {
            switch (jsonObject.get("currency").toString()) {
                case "eur":
                    if (account.getEur().compareTo(amount) < 0) {
                        return respond("Not enough funds!");
                    }
                    account.setEur(account.getEur().subtract(amount));
                    break;
                case "pln":
                    if (account.getPln().compareTo(amount) < 0) {
                        return respond("Not enough funds!");
                    }
                    account.setPln(account.getPln().subtract(amount));
                    break;
                case "pounds":
                    if (account.getPounds().compareTo(amount) < 0) {
                        return respond("Not enough funds!");
                    }
                    account.setPounds(account.getPounds().subtract(amount));
                    break;
            }
        }
        accountRepository.saveAndFlush(account);
        return respond("Transaction successful");
    }

    private ResponseEntity depositTransaction(JSONObject jsonObject) {
        Account account = accountRepository.findAccountById(
                Integer.parseInt(jsonObject.get("accountId").toString()));
        BigDecimal amount = BigDecimal.valueOf(Integer.parseInt(jsonObject.get("amount").toString()));
        switch (jsonObject.get("currency").toString()) {
            case "eur":
                account.setEur(account.getEur().add(amount));
                break;
            case "pln":
                account.setPln(account.getPln().add(amount));
                break;
            case "pounds":
                account.setPounds(account.getPounds().add(amount));
                break;
        }
        accountRepository.saveAndFlush(account);
        return respond("Transaction successful");
    }

    private ResponseEntity sendTransaction(JSONObject jsonObject, User user) {
        Account accountFrom = accountRepository.findAccountById(
                Integer.parseInt(jsonObject.get("accountFrom").toString()));
        if (!accountRepository.findAllByUserEquals(user).contains(accountFrom)) {
            return respond("Account does not belong to user!");
        }
        BigDecimal amount = BigDecimal.valueOf(Integer.parseInt(jsonObject.get("amount").toString()));
        Account accountTo = accountRepository.findAccountById(
                Integer.parseInt(jsonObject.get("accountTo").toString()));
        switch (jsonObject.get("currency").toString()) {
            case "eur":
                if (accountFrom.getEur().compareTo(amount) < 0) {
                    return respond("Not enough funds!");
                }
                accountFrom.setEur(accountFrom.getEur().subtract(amount));
                accountTo.setEur(accountTo.getEur().add(amount));
                break;
            case "pln":
                if (accountFrom.getPln().compareTo(amount) < 0) {
                    return respond("Not enough funds!");
                }
                accountFrom.setPln(accountFrom.getPln().subtract(amount));
                accountTo.setPln(accountTo.getPln().add(amount));
                break;
            case "pounds":
                if (accountFrom.getPounds().compareTo(amount) < 0) {
                    return respond("Not enough funds!");
                }
                accountFrom.setPounds(accountFrom.getPounds().subtract(amount));
                accountTo.setPounds(accountTo.getPounds().add(amount));
                break;
        }
        accountRepository.save(accountFrom);
        accountRepository.saveAndFlush(accountTo);
        return respond("Transaction successful");
    }

    private ResponseEntity changeCurrencyTransaction(JSONObject jsonObject, User user) {
        if (!user.getAccountList().contains(
                accountRepository.findAccountById(Integer.parseInt(jsonObject.get("account").toString())))) {
            return respond("Account doesn't belong to the user!");
        }
        Account account = accountRepository.findAccountById(Integer.parseInt(jsonObject.get("account").toString()));
        BigDecimal amount = BigDecimal.valueOf(Integer.parseInt(jsonObject.get("amount").toString()));
        Multipliers multipliers = multipliersRepository.findMultipliersById(0);
        switch (jsonObject.get("currencyFrom").toString()) {
            case "eur":
                if(account.getEur().compareTo(amount)<0){
                    return respond("Not enough funds!");
                }
                switch(jsonObject.get("currencyTo").toString()){
                    case"pln":
                        account.setPln(account.getPln().add(amount.multiply(BigDecimal.valueOf(multipliers.getEuroToPln()))));
                        account.setEur(account.getEur().subtract(amount));
                        break;
                    case"pounds":
                        account.setPounds(account.getPounds().add(amount.multiply(BigDecimal.valueOf(multipliers.getEuroToPounds()))));
                        account.setEur(account.getEur().subtract(amount));
                        break;
                }
                break;
            case"pln":
                if(account.getPln().compareTo(amount)<0){
                    return respond("Not enough funds!");
                }
                switch(jsonObject.get("currencyTo").toString()){
                    case"eur":
                        account.setPln(account.getEur().add(amount.multiply(BigDecimal.valueOf(multipliers.getPlnToEuro()))));
                        account.setPln(account.getPln().subtract(amount));
                        break;
                    case"pounds":
                        account.setPounds(account.getPounds().add(amount.multiply(BigDecimal.valueOf(multipliers.getPlnToPounds()))));
                        account.setPln(account.getPln().subtract(amount));
                        break;
                }
                break;
            case"Pounds":
                if(account.getPounds().compareTo(amount)<0){
                    return respond("Not enough funds!");
                }
                switch(jsonObject.get("currencyTo").toString()){
                    case"pln":
                        account.setPln(account.getPln().add(amount.multiply(BigDecimal.valueOf(multipliers.getPoundsToPln()))));
                        account.setPounds(account.getPounds().subtract(amount));
                        break;
                    case"euro":
                        account.setEur(account.getEur().add(amount.multiply(BigDecimal.valueOf(multipliers.getPoundsToEuro()))));
                        account.setPounds(account.getPounds().subtract(amount));
                        break;
                }
                break;
        }


        return respond("Transaction successful");
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PutMapping("/transaction")
    public ResponseEntity chooseTransaction(@RequestBody JSONObject jsonObject, @CurrentUserAtt UserPrincipal userPrincipal) {
        if (!jsonObject.containsKey("transactionType"))
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);

        User user = userPrincipal.getUser();
        switch (jsonObject.get("transactionType").toString()) {
            case "withdraw":
                if (!jsonObject.containsKey("currency") || !jsonObject.containsKey("amount") || !jsonObject.containsKey("accountId"))
                    break;
                return withdrawTransaction(jsonObject, user);
            case "deposit":
                if (!jsonObject.containsKey("currency") || !jsonObject.containsKey("amount") || !jsonObject.containsKey("accountId"))
                    break;
                return depositTransaction(jsonObject);
            case "sendCurrency":
                if (!jsonObject.containsKey("currency") || !jsonObject.containsKey("amount")
                        || !jsonObject.containsKey("recieverLogin") || !jsonObject.containsKey("accountId"))
                    break;
                return sendTransaction(jsonObject, user);
            case "changeCurrency":
                if (!jsonObject.containsKey("currencyFrom") || !jsonObject.containsKey("currencyTo")
                        || !jsonObject.containsKey("amount") || !jsonObject.containsKey("accountId"))
                    break;
                return changeCurrencyTransaction(jsonObject, user);
        }
        return new ResponseEntity(null, HttpStatus.OK);
    }
}