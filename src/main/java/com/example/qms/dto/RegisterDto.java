package com.example.qms.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String phone;
    private String name;
    private String password;
    private String email;
}