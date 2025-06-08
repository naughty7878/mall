package com.hd.mall.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

    public class JwtUtils {

        // 动态秘钥（建议使用环境变量或配置文件注入）
//        private static SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        // 固定密钥（至少32个字符（256位）的密钥）
        private static String SECRET_KEY_STRING = "mall-secret-key-1234567890-1234567890-ABCDEF";
        private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

        // Token 有效期（单位：毫秒）
        private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 8; // 8小时

//        // 使用一个密钥来签名 JWT
////    private static final String SECRET_KEY = "mall-secret-key";



        /**
         * 生成 JWT Token
         *
         * @param subject      Token 的主题（通常是用户标识）
         * @param claims       自定义的载荷数据
         * @return JWT Token 字符串
         */
        public static String generateToken(String subject, Map<String, Object> claims) {
            LocalDateTime dateTime = LocalDateTime.now();
            long millis = dateTime.atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            return Jwts.builder()
                    .subject(subject) // 设置主题
                    .claims(claims) // 设置载荷
                    .issuedAt(new Date(millis)) // 签发时间
                    .expiration(new Date(millis + EXPIRATION_TIME)) // 过期时间
                    .signWith(SECRET_KEY) // 使用秘钥签名
                    .compact(); // 生成 Token 字符串
        }

        /**
         * 解析 JWT Token
         *
         * @param token JWT Token 字符串
         * @return 解析后的 JWS 对象
         * @throws JwtException 如果 Token 无效或已过期
         */
        public static Claims parseToken(String token) throws JwtException {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY) // 设置秘钥
                    .build()
                    .parseClaimsJws(token) // 解析 Token
                    .getBody(); // 获取载荷数据
        }

        /**
         * 验证 JWT Token 是否有效
         *
         * @param token JWT Token 字符串
         * @return true 表示有效，false 表示无效
         */
        public static boolean validateToken(String token) {
            try {
                Claims claims = parseToken(token);
                return claims.getExpiration().after(new Date()); // 检查是否过期
            } catch (JwtException e) {
                return false; // Token 无效或解析失败
            }
        }

        /**
         * 从 JWT Token 中获取主题（通常是用户标识）
         *
         * @param token JWT Token 字符串
         * @return Token 的主题
         */
        public static String getSubject(String token) {
            Claims claims = parseToken(token);
            return claims.getSubject();
        }

        public static void main(String[] args) {

            // Step 1: 定义自定义载荷数据
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "ADMIN");
            claims.put("email", "user@example.com");
//            claims.put("sub", "xx");

            // Step 2: 生成 JWT Token
            String subject = "user123"; // Token 的主题（通常是用户 ID 或用户名）
            String token = JwtUtils.generateToken(subject, claims);
//            String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIzIiwicm9sZSI6IkFETUlOIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzQ5MzgyNDU5LCJleHAiOjE3NDk0MTEyNTl9.sHOy-EfsA4C_J8szdms4Sg9-e6oZtTasjEnn7B6cmXs";
            System.out.println("Generated Token: " + token);

            // Step 3: 验证 Token 是否有效
            boolean isValid = JwtUtils.validateToken(token);
            System.out.println("Is Token Valid: " + isValid);

            // Step 4: 解析 Token 并打印载荷数据
            try {
                Claims parsedClaims = JwtUtils.parseToken(token);
                System.out.println("Parsed Claims:");
                System.out.println("Role: " + parsedClaims.get("role"));
                System.out.println("Email: " + parsedClaims.get("email"));
                System.out.println("Subject: " + parsedClaims.getSubject());
                System.out.println("Issued At: " + parsedClaims.getIssuedAt());
                System.out.println("Expiration: " + parsedClaims.getExpiration());
            } catch (JwtException e) {
                System.out.println("Failed to parse token: " + e.getMessage());
            }

            // Step 5: 测试过期 Token
            try {
                // 模拟一个过期的 Token（这里直接等待一段时间也可以）
                Thread.sleep(2000); // 等待 2 秒，确保 Token 过期（如果设置的有效期很短）
                boolean isExpired = JwtUtils.validateToken(token);
                System.out.println("Is Token Valid After Expiration: " + isExpired);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }
    }
