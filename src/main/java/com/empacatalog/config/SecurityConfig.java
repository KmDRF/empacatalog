package com.empacatalog.config;

import com.empacatalog.security.jwt.AuthEntryPointJwt; // Importa el manejador de errores de autenticación
import com.empacatalog.security.jwt.AuthTokenFilter; // Importa el filtro de tokens JWT
import com.empacatalog.security.jwt.JwtUtils; // Importa la utilidad JWT
import com.empacatalog.security.services.UserDetailsServiceImpl; // Importa el servicio de detalles de usuario
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager; // Importa AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Importa DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Importa AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Habilita la seguridad a nivel de método
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Importa BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder; // Importa PasswordEncoder
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Importa el filtro de autenticación de usuario/contraseña

@Configuration // Indica que esta es una clase de configuración de Spring
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring
@EnableMethodSecurity // Habilita la seguridad a nivel de método (ej. @PreAuthorize)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService; // Inyecta nuestro servicio de detalles de usuario
    private final AuthEntryPointJwt unauthorizedHandler; // Inyecta nuestro manejador de errores de autenticación
    private final JwtUtils jwtUtils; // Inyecta nuestra utilidad JWT

    // Constructor para la inyección de dependencias
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtils = jwtUtils;
    }

    // Define el bean para el filtro de autenticación de tokens JWT
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    // Define el bean para el codificador de contraseñas (BCrypt es recomendado)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Define el proveedor de autenticación que usará nuestro UserDetailsService y PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Establece nuestro UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // Establece nuestro PasswordEncoder
        return authProvider;
    }

    // Define el AuthenticationManager, que es el punto de entrada para el proceso de autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Define la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilita la protección CSRF (Cross-Site Request Forgery)
                .csrf(AbstractHttpConfigurer::disable)
                // Configura el manejador de errores para peticiones no autenticadas
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // Configura la política de gestión de sesiones como SIN ESTADO (STATELESS)
                // Esto es crucial para APIs REST que usan JWT, ya que no mantienen estado de sesión en el servidor.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Define las reglas de autorización para las peticiones HTTP
                .authorizeHttpRequests(auth -> auth
                        // Permite el acceso sin autenticación a los endpoints de autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        // Requiere roles específicos para acceder a los endpoints de productos
                        .requestMatchers("/api/products/**").hasAnyRole("ADMIN", "ADVISOR", "CUSTOMER")
                        // Requiere roles específicos para acceder a los endpoints de pedidos
                        .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "ADVISOR", "CUSTOMER")
                        // Requiere el rol PICKER para acceder a los endpoints de picking
                        .requestMatchers("/api/picking/**").hasRole("PICKER")
                        // Requiere el rol REVIEWER para acceder a los endpoints de revisión
                        .requestMatchers("/api/review/**").hasRole("REVIEWER")
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                );

        // Añade nuestro filtro JWT antes del filtro estándar de autenticación de usuario/contraseña de Spring Security.
        // Esto asegura que el token JWT sea validado antes de que Spring intente cualquier otra forma de autenticación.
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Añade nuestro proveedor de autenticación a la configuración de seguridad.
        http.authenticationProvider(authenticationProvider());

        // Construye y retorna la cadena de filtros de seguridad.
        return http.build();
    }
}
