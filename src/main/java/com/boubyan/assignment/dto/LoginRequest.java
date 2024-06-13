package com.boubyan.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest

{

    private String username;
    private String password;
}
