package com.tijetravel.frontend.config;

import com.tijetravel.frontend.dto.AuthUserSession;
import com.tijetravel.frontend.service.BackendApiClient;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final BackendApiClient apiClient;

    public CustomAuthenticationProvider(BackendApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            AuthUserSession sessionUser = apiClient.login(username, password);
            if (sessionUser == null) {
                throw new BadCredentialsException("Credenciales inválidas");
            }

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + sessionUser.getRol());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    sessionUser,
                    password,
                    Collections.singletonList(authority)
            );
            return authToken;
        } catch (Exception e) {
            throw new BadCredentialsException("Credenciales inválidas o error de conexión con el backend", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

