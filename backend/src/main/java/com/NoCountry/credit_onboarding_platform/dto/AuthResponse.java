// package com.NoCountry.credit_onboarding_platform.dto;

// public class AuthResponse {
    
// }

package com.NoCountry.credit_onboarding_platform.dto;

import lombok.*;
import com.NoCountry.credit_onboarding_platform.model.Usuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String email;
    private String nombre;
    private String rol;
    private Long expiresIn; // Milisegundos hasta expiración
    
    public static AuthResponse fromUsuario(Usuario usuario, String token, Long expiresIn) {
        return AuthResponse.builder()
            .token(token)
            .type("Bearer")
            .id(usuario.getId())
            .email(usuario.getEmail())
            .nombre(usuario.getNombre())
            .rol(usuario.getRol().name())
            .expiresIn(expiresIn)
            .build();
    }
}