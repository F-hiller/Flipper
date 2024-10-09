package com.ovg.flipper.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieManager
{
    public String[] resolveTokens(HttpServletRequest request) {
        String[] tokens = {"", ""};
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if ("ACCESS_TOKEN".equals(cookieName)) {
                    tokens[0] = cookie.getValue();
                }
                else if ("REFRESH_TOKEN".equals(cookieName)){
                    tokens[1] = cookie.getValue();
                }
            }
        }
        return tokens;
    }

    public void addCookie (String cookieName, String cookieValue, HttpServletResponse response){
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
