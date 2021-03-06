package com.example.demo.model;


import lombok.*;
import org.springframework.context.annotation.Role;

import javax.persistence.*;
import java.util.List;

@Table(name = "users")
@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private long id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String login;
    @Column
    private String password;
    @OneToMany(mappedBy = "user")
    private List<Account> accountList;
    @Column
    private String role;

}