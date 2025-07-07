package korobov.dev.eventmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(true).ignoring()
                .requestMatchers("/css/**",
                        "/js/**",
                        "/img/**",
                        "/lib/**",
                        "/favicon.ico",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/swagger-config",
                        "/openapi.yaml"
                );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authorizeHttpRequests(authorizeHttpRequest ->
                        authorizeHttpRequest
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/auth").permitAll()
                                .requestMatchers(HttpMethod.GET, "/users/{userId}").hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/locations").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/locations/{locationId}").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/locations/{locationId}").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/locations/**").hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(HttpMethod.POST, "/events").hasAuthority("USER")
                                .requestMatchers(HttpMethod.DELETE, "/events/{locationId}").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/events/{locationId}").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.GET, "/events/**").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/events/search").hasAnyAuthority("ADMIN", "USER")
                                .requestMatchers(HttpMethod.GET, "/events/my").hasAuthority("USER")

                                .requestMatchers(HttpMethod.POST, "/events/registrations/").hasAuthority("USER")
                                .requestMatchers(HttpMethod.DELETE, "/events/registrations/cancel/").hasAuthority("USER")
                                .requestMatchers(HttpMethod.GET, "/events/registrations/my").hasAuthority("USER")

                                .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
