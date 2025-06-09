package com.hd.mall.admin.controller;

import com.hd.mall.admin.api.LoginRequest;
import com.hd.mall.common.api.ApiResponse;
import com.hd.mall.common.api.ResultCode;
import com.hd.mall.common.util.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // 模拟存储token（实际项目用Redis或数据库）
    private final Map<String, UserInfo> tokenStore = new ConcurrentHashMap<>();
    private final String SECRET_KEY = "your-secret-key-123"; // JWT密钥


    @PostMapping("/login")
    public ApiResponse<UserInfo> login(@RequestBody LoginRequest request) {
        // 1. 验证用户名密码（简化版，实际应从数据库验证）
        if (!"admin".equals(request.getUsername()) || !"123456".equals(request.getPassword())) {
            return ApiResponse.failed(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }

        // 2. 生成token（这里使用JWT）
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_ADMIN");
        String token = JwtUtils.generateToken(request.getUsername(), claims);

        // 3. 存储token（实际项目用Redis）
        UserInfo userInfo = new UserInfo(request.getUsername(), "ROLE_ADMIN");
        tokenStore.put(token, userInfo);

        return ApiResponse.success(userInfo);
    }
    
    @PostMapping("/verify")
    public ApiResponse<UserInfo> verifyToken(@RequestHeader("Authorization") String token) {
        // 实际项目中这里会有完整的token验证逻辑
        if (isValidToken(token)) {
            return ApiResponse.success(getUserInfo(token));
        }
        return ApiResponse.failed(ResultCode.UNAUTHORIZED);
    }
    
    private boolean isValidToken(String token) {
        // 实现你的token验证逻辑
        return token != null && token.startsWith("Bearer ");
    }
    
    private UserInfo getUserInfo(String token) {
        // 解析token获取用户信息
        return new UserInfo("user123", "ROLE_USER");
    }
    
    // 用户信息DTO
    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private String username;
        private String role;
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            tokenStore.remove(token.substring(7));
        }
        return ApiResponse.success("登出成功");
    }

}