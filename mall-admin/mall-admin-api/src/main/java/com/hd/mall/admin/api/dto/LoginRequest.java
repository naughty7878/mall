package com.hd.mall.admin.api.dto;

import lombok.Data;

// DTO定义
@Data
public class LoginRequest {
    private String username;
    private String password;
}