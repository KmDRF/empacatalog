package com.empacatalog.application.controller;

import com.empacatalog.application.dto.auth.LoginRequest;
import com.empacatalog.application.dto.auth.RegisterRequest;
import com.empacatalog.application.dto.auth.JwtResponse;
import com.empacatalog.domain.model.ProductAlreadyExistsException;
import com.empacatalog.domain.model.User;
import com.empacatalog.domain.repository.UserRepository;
import com.empacatalog.security.jwt.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Inyección de dependencias a través del constructor
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Endpoint para el inicio de sesión de usuarios.
     * Recibe credenciales, las autentica y devuelve un token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Usa AuthenticationManager para autenticar al usuario con sus credenciales.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // Si la autenticación es exitosa, establece el contexto de seguridad.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Genera el token JWT para el usuario autenticado.
        String jwt = jwtUtils.generateJwtToken((User) authentication.getPrincipal());
        User userDetails = (User) authentication.getPrincipal();

        // Devuelve el token y los detalles del usuario en la respuesta.
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getRole().toString()));
    }

    /**
     * Endpoint para el registro de nuevos usuarios.
     * Recibe los datos del usuario, los valida y los guarda en la base de datos.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Verifica si el email ya está en uso.
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ProductAlreadyExistsException("El email '" + registerRequest.getEmail() + "' ya está registrado.");
        }

        // Crea una nueva entidad de usuario.
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Codifica la contraseña
        user.setRole(registerRequest.getRole());

        // Guarda el nuevo usuario en la base de datos.
        userRepository.save(user);

        return ResponseEntity.ok("Usuario registrado exitosamente!");
    }
}
