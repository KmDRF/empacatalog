package com.empacatalog.security.jwt;

import com.empacatalog.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Este filtro se ejecuta una vez por cada petición HTTP para validar el token JWT.
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    // Constructor para inyectar las dependencias.
    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    // Este método es el corazón del filtro. Se ejecuta en cada petición que llega a tu backend.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // --- ¡CÓDIGO AÑADIDO! ---
        // Se obtiene la URL de la petición.
        String path = request.getRequestURI();
        // Se comprueba si la petición es para un endpoint de autenticación (/api/auth/).
        if (path.startsWith("/api/auth/")) {
            // Si es así, se deja pasar la petición sin procesar el token.
            filterChain.doFilter(request, response);
            return; // Se detiene la ejecución del filtro aquí.
        }
        // -------------------------

        try {
            // 1. Se obtiene el token JWT de la cabecera de la petición.
            String jwt = parseJwt(request);

            // 2. Si el token existe y es válido, se procesa la autenticación.
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Se extrae el nombre de usuario (email) del token.
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Se cargan los detalles del usuario desde el servicio.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Se crea un objeto de autenticación para Spring Security.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Se establecen detalles adicionales (como la dirección IP) en la autenticación.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Se establece la autenticación en el contexto de seguridad.
                // Esto le dice a Spring Security que el usuario está autenticado para esta petición.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Se registra cualquier error que ocurra durante la autenticación.
            logger.error("No se puede establecer la autenticación del usuario: {}", e.getMessage());
        }

        // Se continúa con la cadena de filtros de seguridad de Spring.
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extraer el token de la cabecera "Authorization" (formato "Bearer <token>").
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Retorna solo el token.
        }
        return null; // Retorna nulo si no hay token.
    }
}