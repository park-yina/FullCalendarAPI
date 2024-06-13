package com.example.config;


import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        String uri = request.getRequestURI();
        
        if (session != null && session.getAttribute("username") != null) {
            if ("/user/login".equals(uri)) {
                response.sendRedirect("/"); // 로그인한 사용자가 로그인 페이지에 접근 시 홈으로 리다이렉트
                return false;
            }
            return true; // 로그인한 사용자가 다른 페이지에 접근 시 요청 허용
        } else {
            if ("/user/mypage".equals(uri)) {
                response.sendRedirect("/user/login"); // 로그인되지 않은 사용자가 마이페이지에 접근 시 로그인 페이지로 리다이렉트
                return false;
            }
            if("/board".equals(uri)){
                response.sendRedirect("/user/login"); // 로그인되지 않은 사용자가 마이페이지에 접근 시 로그인 페이지로 리다이렉트
                return false;
            }
            return true; // 로그인되지 않은 사용자가 다른 페이지에 접근 시 요청 허용
        }
    }
}