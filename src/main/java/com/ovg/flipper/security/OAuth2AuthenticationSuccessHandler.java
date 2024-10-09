package com.ovg.flipper.security;

import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.repository.UserRepository;
import com.ovg.flipper.util.CookieManager;
import com.ovg.flipper.util.JwtManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtManager jwtManager;
    private final CookieManager cookieManager;
    private final UserRepository userRepository;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(JwtManager jwtManager, CookieManager cookieManager, UserRepository userRepository){
        this.jwtManager = jwtManager;
        this.cookieManager = cookieManager;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();

        UserAuthDto userAuthDto = jwtManager.generateTokens(email, userRepository.findByEmail(email).orElseThrow().getUserId());

        cookieManager.addCookie("ACCESS_TOKEN", userAuthDto.getAccessToken(), response);
        cookieManager.addCookie("REFRESH_TOKEN", userAuthDto.getRefreshToken(), response);

        log.info("test : {}, {}", userAuthDto.getAccessToken(), userAuthDto.getRefreshToken());

        response.sendRedirect("/mypage");
    }
}
