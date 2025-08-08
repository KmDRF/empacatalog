package com.empacatalog.application.dto.auth;

import com.empacatalog.domain.model.User; // Importa la clase User para el enum Role
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;    // Email del nuevo usuario
    private String password; // Contraseña del nuevo usuario
    private User.Role role;  // Rol del nuevo usuario (ADMIN, ADVISOR, CUSTOMER, etc.)
}
