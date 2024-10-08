package com.ovg.flipper.config;

import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.service.CustomUserDetailsService;
import com.ovg.flipper.util.CookieManager;
import com.ovg.flipper.util.JwtManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    private final JwtManager jwtManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final CookieManager cookieManager;

    public AuthenticationTokenFilter(JwtManager jwtManager, CustomUserDetailsService customUserDetailsService, CookieManager cookieManager) {
        this.jwtManager = jwtManager;
        this.customUserDetailsService = customUserDetailsService;
        this.cookieManager = cookieManager;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String[] tokens = cookieManager.resolveTokens(request);
        String accessToken = tokens[0];
        String refreshToken = tokens[1];
        String token = "";

        if(jwtManager.validateToken(accessToken)){
            token = accessToken;
        }
        else if (jwtManager.validateToken(refreshToken) && jwtManager.checkRefreshTokenExists(refreshToken)){
            UserAuthDto userAuthDto = jwtManager.generateTokens(refreshToken);
            cookieManager.addCookie("ACCESS_TOKEN", userAuthDto.getAccessToken(), response);
            cookieManager.addCookie("REFRESH_TOKEN", userAuthDto.getRefreshToken(), response);
            token = userAuthDto.getAccessToken();
        }

        if(token.isBlank()){
            String userName = jwtManager.getUserName(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
