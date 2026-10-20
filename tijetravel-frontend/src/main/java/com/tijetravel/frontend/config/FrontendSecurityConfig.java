package com.tijetravel.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;

@Configuration
@EnableWebSecurity
public class FrontendSecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;

    public FrontendSecurityConfig(CustomAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .headers(headers -> headers
                        .cacheControl(cache -> {})
                )
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos
                        .requestMatchers("/css/**", "/images/**", "/static/**", "/favicon.ico").permitAll()

                        // Rutas públicas de consulta
                        .requestMatchers("/", "/buscar", "/vuelos", "/hoteles", "/sucursales", "/login", "/registro").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Panel de Administrador
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")

                        // Panel de Vendedor y Administrador
                        .requestMatchers("/gestion/**", "/reservas-admin/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")

                        // Zonas autenticadas de clientes y usuarios
                        .requestMatchers("/mi-cuenta/**", "/reservas/**", "/familia/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/mi-cuenta", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}

