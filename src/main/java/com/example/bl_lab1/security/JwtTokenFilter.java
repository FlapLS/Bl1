package com.example.bl_lab1.security;

import com.example.bl_lab1.service.AuthenticationService;
import com.example.bl_lab1.utils.CustomUserDetails;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Lazy
public class JwtTokenFilter extends OncePerRequestFilter {
    
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationService authenticationService;
    
    @Getter
    private String token;
    
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    @NotNull HttpServletResponse httpServletResponse,
                                    @NotNull FilterChain filterChain
                                   ) throws ServletException, IOException {
        //обнуление токена перед фильтрацией от предыдущей авторизации
        token = null;

        // Получение заголовка авторизации и подтверждение
        final String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        
        // Получение jwt токена
        token = header.split(" ")[1].trim();
        
        //Получение сущности пользователя
        CustomUserDetails userDetails;
        try {
            userDetails = authenticationService.loadUserByUsername(jwtTokenUtil.getUsernameFromToken(token));
        }
        catch (io.jsonwebtoken.SignatureException exception) {
            return;
        }
        
        //Подтверждение токена
        if (!jwtTokenUtil.validateToken(token, userDetails)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        
        //установка сущности пользователя на spring security context
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                                                                                                     null,
                                                                                                     userDetails == null ? List.of() :
                                                                                                     userDetails.getAuthorities()
        );
        
        authentication.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                                 );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
