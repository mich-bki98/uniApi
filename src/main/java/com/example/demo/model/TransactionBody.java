package com.example.demo.model;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Builder

public class TransactionBody {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String Operation;
    @Column
    private String currency;
    @Column
    private BigDecimal amount;
    @Column
    Account accountFrom;
    @Column
    Account accountTo;

}
