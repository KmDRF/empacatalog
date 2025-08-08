package com.empacatalog.application.dto.auth; // Corregido el paquete

import lombok.Data; // Importa Lombok para simplificar el código

@Data // Anotación de Lombok para generar automáticamente getters, setters, equals, hashCode y toString
public class LoginRequest {
    private String email;    // Campo para el email del usuario
    private String password; // Campo para la contraseña del usuario
}
