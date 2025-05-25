package org.pipproject.pip_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for application security settings.
 */
@Configuration
public class SecurityConfig {

    /**
     * Defines the password encoder bean using BCrypt hashing algorithm.
     *
     * @return a {@link PasswordEncoder} instance using {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP security.
     * <p>
     * - Allows all requests without authentication.<br>
     * - Disables CSRF protection.<br>
     * - Disables the default login form.<br>
     * - Disables HTTP Basic authentication.
     * </p>
     *
     * @param http the {@link HttpSecurity} object to configure
     * @return a configured {@link SecurityFilterChain} instance
     * @throws Exception in case of configuration errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow all requests without authentication
                )
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF (optional, useful for APIs)
                .formLogin(AbstractHttpConfigurer::disable) // Disable default login form
                .httpBasic(AbstractHttpConfigurer::disable); // Disable Basic Auth

        return http.build();
    }
}
