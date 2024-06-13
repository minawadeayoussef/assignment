package com.boubyan.assignment.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user_app")
@Data
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
}
