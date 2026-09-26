package com.maaztausif.khallikarao.config;

import com.maaztausif.khallikarao.repository.AuthRepo;
import com.maaztausif.khallikarao.config.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthRepo repo;

    public JwtFilter(JwtService jwtService, AuthRepo repo) {
        this.jwtService = jwtService;
        this.repo = repo;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return "/api/auth/signup".equals(path)
                || "/api/auth/login".equals(path)
                || "/api/auth/send-otp".equals(path)
                || "/api/auth/verify-email".equals(path)
                || "/api/auth/forgot-password".equals(path)
                || "/api/auth/reset-password".equals(path)
                || "/api/auth/send-reset-otp".equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        JwtService.TokenData tokenData;

        try {
            tokenData = jwtService.readToken(header.substring(7));
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token"
            );
            return;
        }

        long userId = tokenData.userId();
        var existingUser = repo.findById(userId);

        if (existingUser.isEmpty()
                || !existingUser.get().isEmailVerified()
                || existingUser.get().getTokenVersion()
                != tokenData.tokenVersion()) {
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in again"
            );
            return;
        }

        var authentication = new UsernamePasswordAuthenticationToken(
                Long.toString(userId),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        chain.doFilter(request, response);
    }
}