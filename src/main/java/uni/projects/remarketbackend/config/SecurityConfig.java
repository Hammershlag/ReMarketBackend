package uni.projects.remarketbackend.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uni.projects.remarketbackend.config.jwt.JwtAuthenticationEntryPoint;
import uni.projects.remarketbackend.config.jwt.JwtAuthenticationFilter;

import java.util.logging.Logger;


/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */
@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private UserDetailsService userDetailsService;

    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    private JwtAuthenticationFilter authenticationFilter;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> {
                    // Allow all Swagger & OpenAPI-related URLs
                    authorize.requestMatchers("/swagger-ui/**").permitAll();
                    authorize.requestMatchers("/v3/api-docs/**").permitAll();
                    authorize.requestMatchers("/v3/api-docs").permitAll();
                    authorize.requestMatchers("/swagger-ui.html").permitAll();

                    authorize.requestMatchers("/api/auth/**").permitAll();
                    authorize.requestMatchers("/api/accounts").authenticated();
                    authorize.requestMatchers("/api/photo/user").authenticated();

                    authorize.requestMatchers(HttpMethod.POST, "/api/listings/{id}/reviews").authenticated();
                    authorize.requestMatchers(HttpMethod.PUT, "/api/listings/{id}/reviews").authenticated();
                    authorize.requestMatchers(HttpMethod.DELETE, "/api/listings/{id}/reviews").authenticated();

                    // Listing photo endpoints
                    authorize.requestMatchers(HttpMethod.POST, "/api/photo/listing").hasAnyRole("SELLER", "ADMIN", "STUFF");
                    authorize.requestMatchers(HttpMethod.GET, "/api/photo/listing/**").permitAll();


                    authorize.requestMatchers(HttpMethod.GET, "/api/listing").permitAll();
                    authorize.requestMatchers("/api/listing").hasAnyRole("SELLER", "ADMIN", "STUFF");
                    authorize.requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll();
                    authorize.requestMatchers("/api/categories/**").hasAnyRole("SELLER", "ADMIN", "STUFF");
                    authorize.requestMatchers("/api/wishlists").authenticated();
                    authorize.requestMatchers("/api/shopping-carts").authenticated();
                    authorize.requestMatchers("/api/orders").authenticated();

                    authorize.requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "STUFF");

                    authorize.requestMatchers("/actuator/**").hasRole("ADMIN");
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    authorize.anyRequest().authenticated();

                })                .cors(Customizer.withDefaults()) // Enable CORS
                .httpBasic(Customizer.withDefaults()); // Enable HTTP Basic authentication

        http.exceptionHandling( exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint));

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
