package com.projekat.digital_archive.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception -> exception

                        // Korisnik nije prijavljen ili token nije validan
                        .authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.setStatus(
                                                HttpServletResponse
                                                        .SC_UNAUTHORIZED
                                        )
                        )

                        // Korisnik je prijavljen, ali nema potrebnu ulogu
                        .accessDeniedHandler(
                                (request, response,
                                 accessDeniedException) ->
                                        response.setStatus(
                                                HttpServletResponse
                                                        .SC_FORBIDDEN
                                        )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * CORS preflight zahtjevi
                         */
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        /*
                         * LOGIN
                         * Dostupan je svim korisnicima.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/login"
                        )
                        .permitAll()

                        /*
                         * Ostali /auth endpoint-i nijesu dostupni.
                         * Kreiranje korisnika ide preko /api/users.
                         */
                        .requestMatchers("/auth/**")
                        .denyAll()

                        /*
                         * USERS
                         * Samo ADMIN može kreirati i pregledati korisnike.
                         */
                        .requestMatchers(
                                "/api/users",
                                "/api/users/**"
                        )
                        .hasRole("ADMIN")

                        /*
                         * SEARCH I OPEN PDF
                         * GET zahtjeve mogu koristiti sve tri uloge.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/documents",
                                "/api/documents/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "INDEXER",
                                "OPERATOR"
                        )

                        /*
                         * CREATE I UPLOAD
                         * ADMIN i INDEXER mogu kreirati dokumente.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/documents",
                                "/api/documents/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "INDEXER"
                        )

                        /*
                         * EDIT
                         * ADMIN i INDEXER mogu mijenjati dokumente.
                         */
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/documents",
                                "/api/documents/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "INDEXER"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/documents",
                                "/api/documents/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "INDEXER"
                        )

                        /*
                         * DELETE
                         * Samo ADMIN može brisati dokumente.
                         */
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/documents",
                                "/api/documents/**"
                        )
                        .hasRole("ADMIN")

                        /*
                         * Svi ostali zahtjevi zahtijevaju validan JWT.
                         */
                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.addAllowedOrigin(
                "http://localhost:4200"
        );

        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}