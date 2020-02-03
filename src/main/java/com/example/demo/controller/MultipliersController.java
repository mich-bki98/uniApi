package com.example.demo.controller;

import com.example.demo.model.Multipliers;
import com.example.demo.repository.MultipilersRepository;
import com.example.demo.security.CurrentUserAtt;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MultipliersController {

    MultipilersRepository multipilersRepository;

    @Secured("ROLE_ADMIN")
    @PostMapping("/multipliers")
    public void createMultsTable(@RequestBody JSONObject jsonObject) {
        Multipliers multipliers = Multipliers.builder()
                .euroToPln((Double) jsonObject.get("euroToPln"))
                .plnToEuro((Double) jsonObject.get("plnToEuro"))
                .plnToPounds((Double) jsonObject.get("plnToPounds"))
                .poundsToPln((Double) jsonObject.get("poundsToPln"))
                .euroToPounds((Double) jsonObject.get("euroToPounds"))
                .poundsToEuro((Double) jsonObject.get("poundsToEuro"))
                .build();
        multipilersRepository.saveAndFlush(multipliers);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/multipliers")
    public void updateMultsTable(@RequestBody JSONObject jsonObject) {
        Multipliers multipliers = multipilersRepository.getOne((Long) jsonObject.get("multiplierID"));
        multipilersRepository.delete(multipliers);
        if(jsonObject.containsKey("euroToPln"))
        multipliers.setEuroToPln((Double) jsonObject.get("euroToPln"));
        if(jsonObject.containsKey("plnToEuro"))
        multipliers.setPlnToEuro((Double) jsonObject.get("plnToEuro"));
        if(jsonObject.containsKey("plnToPounds"))
        multipliers.setPlnToPounds((Double) jsonObject.get("plnToPounds"));
        if(jsonObject.containsKey("poundsToPln"))
        multipliers.setPoundsToPln((Double) jsonObject.get("poundsToPln"));
        if(jsonObject.containsKey("euroToPounds"))
        multipliers.setEuroToPounds((Double) jsonObject.get("euroToPounds"));
        if(jsonObject.containsKey("poundsToEuro"))
        multipliers.setPoundsToEuro((Double) jsonObject.get("poundsToEuro"));
        multipilersRepository.saveAndFlush(multipliers);
    }

    @Secured("ROLE_USER")
    @GetMapping("/multipliers")
    public JSONObject getMultsTable() {
        JSONObject jsonObject = new JSONObject();
        Multipliers multipliers = multipilersRepository.getOne((long) 0);
        jsonObject.put("plnToEuro: ", multipliers.getPlnToEuro());
        jsonObject.put("euroToPln: ", multipliers.getEuroToPln());
        jsonObject.put("plnToPounds: ", multipliers.getPlnToPounds());
        jsonObject.put("poundsToPln: ", multipliers.getPoundsToPln());
        jsonObject.put("euroToPounds: ", multipliers.getEuroToPounds());
        jsonObject.put("poundsToEuro: ", multipliers.getPoundsToEuro());
        return jsonObject;
    }
}