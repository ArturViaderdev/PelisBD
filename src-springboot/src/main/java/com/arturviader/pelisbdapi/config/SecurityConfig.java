package com.arturviader.pelisbdapi.config;

import com.arturviader.pelisbdapi.security.JwtAuthenticationFilter;
import com.arturviader.pelisbdapi.security.JwtUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, JwtUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Rutas públicas
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/confirmemail",
                                "/error",
                                "/api/movies/popular/{page}", //Endpoint que no detecta el login, necesita poder ser accedido tanto desde usuario no logeado como logeado
                                "/api/movies/{id}",
                                "/api/movies/trending",
                                "/api/movies/categories",
                                "/api/movies/category/{page}",
                                "/api/tv/popular/{page}",
                                "/api/tv/trending",
                                "/api/tv/{id}",
                                "/api/tv/category/{id}",
                                "/api/tv/categories",
                                "/api/tv/{id}/season/{seasonNumber}",
                                "/api/tv/{id}/season/{seasonNumber}/episode/{episodeNumber}",
                                "/api/search/multi",
                                "/api/search/movie",
                                "/api/search/tv"
                        ).permitAll()

                        // ✅ Rutas protegidas: requieren ROLE_USER
                        //endpoints que detectan el login
                        .requestMatchers(
                                "/api/user/watched",
                                "/api/user/watched/{type}/{itemId}",
                                "/api/user/watched/status/{type}/{tmbdId}",
                                "/api/user/watchlist",
                                "/api/user/watchlist/{type}/{itemId}",
                                "/api/user/watchlist/status/{type}/{tmbdId}",
                                "/api/user/tv/{tvId}/episode",
                                "/api/user/tv/{tvId}/season",
                                "/api/user/tv/{tvId}",
                                "/api/user/watched/{tmdbId}",
                                "/api/user/watchlist/{tmbdId}",
                                "/api/user/watchlist/{type}/{itemId}",
                                "/api/reviews/{type}/{id}/rate",
                                "/api/reviews/{type}/{id}/ratings"

                        ).hasAnyAuthority("ROLE_USER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
