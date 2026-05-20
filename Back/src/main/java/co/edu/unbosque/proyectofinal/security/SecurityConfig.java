package co.edu.unbosque.proyectofinal.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication
        .AuthenticationManager;

import org.springframework.security.authentication
        .AuthenticationProvider;

import org.springframework.security.authentication.dao
        .DaoAuthenticationProvider;

import org.springframework.security.config
        .Customizer;

import org.springframework.security.config.annotation.authentication.configuration
        .AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders
        .HttpSecurity;

import org.springframework.security.config.annotation.web.configuration
        .EnableWebSecurity;

import org.springframework.security.config.http
        .SessionCreationPolicy;

import org.springframework.security.core.userdetails
        .UserDetailsService;

import org.springframework.security.crypto.bcrypt
        .BCryptPasswordEncoder;

import org.springframework.security.crypto.password
        .PasswordEncoder;

import org.springframework.security.web
        .SecurityFilterChain;

import org.springframework.security.web.authentication
        .UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors
        .CorsConfiguration;

import org.springframework.web.cors
        .CorsConfigurationSource;

import org.springframework.web.cors
        .UrlBasedCorsConfigurationSource;


/**
 * Configuración general
 * de Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            UserDetailsService userDetailsService) {

        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configuración principal
     * de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)

            throws Exception {

        http

                /**
                 * Habilita CORS.
                 */
                .cors(Customizer.withDefaults())

                /**
                 * Deshabilita CSRF.
                 */
                .csrf(csrf -> csrf.disable())

                /**
                 * Configuración de rutas.
                 */
                .authorizeHttpRequests(auth -> auth

                        /**
                         * Endpoints públicos.
                         */
                        .requestMatchers(
                                "/auth/**")

                        .permitAll()

                        /**
                         * Swagger.
                         */
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error")

                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/usuarios")
                        .permitAll()
                        
                        .requestMatchers("/admin/**")
                        .hasAuthority("ROLE_ADMIN")
                        
                        /**
                         * TODO:
                         * Configurar permisos
                         * más adelante.
                         */
                        .anyRequest()

                        .authenticated())

                /**
                 * JWT = stateless.
                 */
                .sessionManagement(session ->

                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                /**
                 * Provider.
                 */
                .authenticationProvider(
                        authenticationProvider())

                /**
                 * Filtro JWT.
                 */
                .addFilterBefore(

                        jwtAuthFilter,

                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provider de autenticación.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(
                        userDetailsService);

        authProvider.setPasswordEncoder(
                passwordEncoder());

        return authProvider;
    }

    /**
     * AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)

            throws Exception {

        return config.getAuthenticationManager();
    }

    /**
     * Encoder BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración CORS.
     */
    @Bean
    public CorsConfigurationSource
            corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(

                List.of(
                        "http://localhost:4200"));

        configuration.setAllowedMethods(

                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"));

        configuration.setAllowedHeaders(
                List.of("*"));

        configuration.setAllowCredentials(
                true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration);

        return source;
    }
}