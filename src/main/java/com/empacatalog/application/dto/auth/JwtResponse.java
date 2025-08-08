package com.empacatalog.application.dto.auth;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;      // El token JWT generado
    private String type = "Bearer"; // Tipo de token (estándar es "Bearer")
    private Long id;           // ID del usuario
    private String email;      // Email del usuario
    private String role;       // Rol del usuario

    public JwtResponse(String accessToken, Long id, String email, String role) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.role = role;
    }
}
