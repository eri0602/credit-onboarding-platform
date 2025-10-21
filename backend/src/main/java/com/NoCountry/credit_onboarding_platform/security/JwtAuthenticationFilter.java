package com.NoCountry.credit_onboarding_platform.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            // Extraer JWT del header Authorization
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                // Extraer username (email) del token
                String username = jwtUtil.extractUsername(jwt);
                
                // Cargar detalles del usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validar token con UserDetails
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Crear autenticación
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Establecer autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("No se pudo establecer la autenticación del usuario", e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    // Extraer JWT del header Authorization: Bearer <token>
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }

    // @Override
    // protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
    //         jakarta.servlet.http.HttpServletResponse response, FilterChain filterChain)
    //         throws jakarta.servlet.ServletException, IOException {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
    // }
}