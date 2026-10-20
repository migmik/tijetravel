package com.tijetravel.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas para visitantes y catálogo abierto
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Rutas exclusivas del administrador
                        .requestMatchers("/api/v1/balance/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMINISTRADOR")

                        // Gestión de vuelos, hoteles y sucursales (crear, editar, borrar)
                        .requestMatchers(HttpMethod.POST, "/api/v1/vuelos/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/vuelos/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/vuelos/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST, "/api/v1/hoteles/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/hoteles/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/hoteles/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")

                        .requestMatchers(HttpMethod.POST, "/api/v1/sucursales/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/sucursales/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/sucursales/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")

                        // Lecturas y operaciones autenticadas
                        .requestMatchers("/api/v1/turistas/**").authenticated()
                        .requestMatchers("/api/v1/reservas/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vuelos/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/hoteles/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/sucursales/**").authenticated()

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

