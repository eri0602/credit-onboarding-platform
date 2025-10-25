// package com.NoCountry.credit_onboarding_platform.dto;

// public class AuthRequest {
    
// }

package com.NoCountry.credit_onboarding_platform.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
}