package com.example.demo.model;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity
@Table
public class Multipliers {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private double plnToEuro;
    @Column
    private double euroToPln;
    @Column
    private double plnToPounds;
    @Column
    private double poundsToPln;
    @Column
    private double euroToPounds;
    @Column
    private double poundsToEuro;
}
