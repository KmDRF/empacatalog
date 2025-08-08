package com.empacatalog.security.jwt; // Paquete donde se encuentra JwtUtils

import com.empacatalog.security.services.UserDetailsServiceImpl; // Importa el servicio de detalles del usuario
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

// Este filtro se ejecutará una vez por cada petición HTTP.
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtils jwtUtils; // Inyección de dependencia de JwtUtils
    private final UserDetailsServiceImpl userDetailsService; // Inyección de dependencia de UserDetailsServiceImpl

    // Constructor para inyectar las dependencias.
    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Obtiene el token JWT de la cabecera de la petición.
            String jwt = parseJwt(request);

            // 2. Si hay un token y es válido, procesa la autenticación.
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Extrae el nombre de usuario (email) del token.
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Carga los detalles del usuario usando UserDetailsServiceImpl.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Crea un objeto de autenticación.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Establece detalles adicionales de la autenticación (como la dirección IP).
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establece el objeto de autenticación en el SecurityContextHolder.
                // Esto indica a Spring Security que el usuario está autenticado para esta petición.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("No se puede establecer la autenticación del usuario: {}", e.getMessage());
        }

        // Continúa la cadena de filtros de seguridad de Spring.
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extraer el token JWT de la cabecera "Authorization".
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        // El token JWT suele venir en el formato "Bearer <token>".
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Retorna solo el token, sin "Bearer "
        }
        return null;
    }
}
