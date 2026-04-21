package com.Scoders.BankingApp.security;

import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class SessionFilter implements Filter {

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/", "/login", "/register", "/captcha", "/surname",
            "/css/", "/js/", "/image/", "/support-guest"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        
        if (isExcludedPath(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpSession session = httpRequest.getSession(false);
        
        if (session == null) {
            httpResponse.sendRedirect("/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            httpResponse.sendRedirect("/login");
            return;
        }
        
        String currentSessionId = session.getId();
        User userFromDb = UserDatabase.getUserById(currentUser.getId());
        
        if (userFromDb == null || !currentSessionId.equals(userFromDb.getSessionId())) {
            session.removeAttribute("currentUser");
            httpResponse.sendRedirect("/login?message=您的账号已在其他设备登录，请重新登录");
            return;
        }
        
        chain.doFilter(request, response);
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
