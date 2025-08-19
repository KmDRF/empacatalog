package com.empacatalog.config;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Proporciona el usuario actual para los campos de auditoría.
 * Se integra con Spring Security para obtener el nombre del usuario autenticado.
 */
@Component
@Primary // <-- Esta es la anotación que faltaba
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Obtiene el objeto de autenticación del contexto de seguridad.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        // Retorna el nombre del usuario (principal).
        return Optional.of(authentication.getName());
    }
}
