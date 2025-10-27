package com.example.autofixpro.config;

import com.example.autofixpro.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UsuarioService usuarioService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(@Lazy UsuarioService usuarioService,
                         CustomAuthenticationSuccessHandler successHandler) {
        this.usuarioService = usuarioService;
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Permitir acceso a recursos estáticos
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/imagenes/**", "/favicon.ico", "/*.html").permitAll()
                // Permitir acceso público a login, registro y páginas de consulta
                .requestMatchers("/", "/index", "/web", "/login", "/register", "/consultar", "/api/auth/**").permitAll()
                // WebSocket endpoints - permitir acceso (la seguridad se maneja a nivel de usuario)
                .requestMatchers("/ws-notifications/**", "/app/**", "/topic/**", "/queue/**").permitAll()
                // Permitir acceso a la API REST sin autenticación (considerar restringir en producción)
                .requestMatchers("/api/**").permitAll()
                // H2 Console solo para desarrollo
                .requestMatchers("/h2-console/**").permitAll()
                // Rutas administrativas - solo ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Rutas del dashboard principal - ADMIN, TECNICO, RECEPCIONISTA
                .requestMatchers("/dashboard", "/ordenes/**", "/vehiculos/**").hasAnyRole("ADMIN", "TECNICO", "RECEPCIONISTA")
                // Rutas de clientes - solo USER
                .requestMatchers("/cliente-dashboard", "/cliente/**").hasRole("USER")
                // Rutas de técnicos
                .requestMatchers("/tecnico/**").hasRole("TECNICO")
                // Todas las demás requieren autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new OrRequestMatcher(
                    new AntPathRequestMatcher("/logout", "GET"),
                    new AntPathRequestMatcher("/logout", "POST")
                ))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**", "/ws-notifications/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
