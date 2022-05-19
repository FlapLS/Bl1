package com.example.bl_lab1.security;

import com.example.bl_lab1.utils.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Lazy
public class JwtTokenUtil implements Serializable {
    
    public static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60; //1 неделя
    private static final long serialVersionUID = -2550185165626007488L;
    private static final ObjectMapper objectMapper = getDefaultObjectMapper();
    private final String secret = "zdtlD3JK56m6wTTgsNFhqzjqP";
    
    private static ObjectMapper getDefaultObjectMapper() {
        return new ObjectMapper();
    }
    
    //получение логина пользователя из jwt токена
    public String getUsernameFromToken(String token) {
        String subject = getClaimFromToken(token, Claims::getSubject);
        JsonNode subjectJSON = null;
        try {
            subjectJSON = objectMapper.readTree(subject);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (subjectJSON != null) {return subjectJSON.get("user_login").asText();}
        else {return null;}
    }
    
    //получение id пользователя из jwt токена
    public String getUserIdFromToken(String token) {
        String subject = getClaimFromToken(token, Claims::getSubject);
        JsonNode subjectJSON = null;
        try {
            subjectJSON = objectMapper.readTree(subject);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (subjectJSON != null) {return subjectJSON.get("user_id").asText();}
        else {return "";}
    }
    
    //получение даты истечения срока из jwt токена
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    //получение фиксированной информации из токена
    public <T> T getClaimFromToken(String token,
                                   Function<Claims, T> claimsResolver
                                  ) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    //для получения любой информации из токена, необходим секретный ключ
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    
    //проверка истекло ли время действия токена
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    //генерация токена для пользователя
    public String generateToken(CustomUserDetails customUserDetails) throws JsonProcessingException {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, customUserDetails.toString());
    }
    
    //пока создается токен -
    //1. Определение заявленной информации о токене: Issuer, Expiration, Subject, и ID
    //2. Связывание JWT используя HS512 алгоритм и секретный ключ.
    //3. Согласно компактной сериализации JWS (https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   уплотнение JWT в URL-безопасную строку
    private String doGenerateToken(Map<String, Object> claims,
                                   String subject
                                  ) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
              .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
              .signWith(SignatureAlgorithm.HS512, secret).compact();
    }
    
    //подтверждение токена
    public Boolean validateToken(String token,
                                 CustomUserDetails userDetails
                                ) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
