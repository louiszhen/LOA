package com.yunshang.common.jwt;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author nanfeng
 * @description Jwt工具类
 * @date 2023-03-13 11:25
 */
public class JwtHelper {

    private static final long TOKEN_EXPIRATION = 365L * 24 * 60 * 60 * 1000;
    private static final String TOKEN_SIGN_KEY = "123456";

    public static String createToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject("AUTH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("username", username)
                .signWith(SignatureAlgorithm.HS512, TOKEN_SIGN_KEY)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    public static Long getUserId(String token) {
        try {
            if (!StringUtils.hasLength(token)) {
                return null;
            }
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
            Claims jwsBody = claimsJws.getBody();
            return Long.valueOf(jwsBody.get("userId").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUsername(String token) {
        try {
            if (!StringUtils.hasLength(token)) {
                return null;
            }
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
            Claims jwsBody = claimsJws.getBody();
            return (String) jwsBody.get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
