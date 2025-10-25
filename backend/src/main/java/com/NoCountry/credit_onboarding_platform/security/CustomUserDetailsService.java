package com.NoCountry.credit_onboarding_platform.security;

import com.NoCountry.credit_onboarding_platform.model.Usuario;
import com.NoCountry.credit_onboarding_platform.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        
        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .authorities(getAuthorities(usuario))
            .accountExpired(false)
            .accountLocked(usuario.getEstado() == Usuario.EstadoUsuario.BLOQUEADO)
            .credentialsExpired(false)
            .disabled(usuario.getEstado() == Usuario.EstadoUsuario.INACTIVO)
            .build();
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );
    }
}