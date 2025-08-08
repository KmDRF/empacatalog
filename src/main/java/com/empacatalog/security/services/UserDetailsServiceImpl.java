package com.empacatalog.security.services; // Este es el paquete correcto para esta clase

import com.empacatalog.domain.model.User; // Importa la entidad User que creamos
import com.empacatalog.domain.repository.UserRepository; // Importa el repositorio de User
import org.springframework.security.core.userdetails.UserDetails; // Interfaz principal de Spring Security para detalles del usuario
import org.springframework.security.core.userdetails.UserDetailsService; // Interfaz de servicio para cargar usuarios
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Excepción para cuando el usuario no se encuentra
import org.springframework.stereotype.Service; // Anotación para marcar esta clase como un servicio de Spring
import org.springframework.transaction.annotation.Transactional; // Anotación para control transaccional

@Service // Marca esta clase como un componente de servicio de Spring. Esto permite que Spring la detecte y la inyecte.
public class UserDetailsServiceImpl implements UserDetailsService { // Implementa la interfaz UserDetailsService de Spring Security

    private final UserRepository userRepository; // Declara una dependencia al UserRepository

    // Constructor para la inyección de dependencias. Spring inyectará automáticamente una instancia de UserRepository.
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Este método es la implementación central de la interfaz UserDetailsService.
     * Es llamado por Spring Security durante el proceso de autenticación para cargar los detalles de un usuario.
     *
     * @param email El "nombre de usuario" (en nuestro caso, el email) que se intenta autenticar.
     * @return Un objeto UserDetails que contiene la información del usuario (credenciales, roles, etc.).
     * @throws UsernameNotFoundException Si el usuario con el email proporcionado no se encuentra en la base de datos.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz UserDetailsService
    @Transactional(readOnly = true) // Anotación para control transaccional. readOnly = true optimiza las operaciones de lectura.
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca el usuario en la base de datos utilizando el UserRepository.
        // El método findByEmail devuelve un Optional<User>.
        User user = userRepository.findByEmail(email)
                // Si el Optional está vacío (el usuario no se encontró), lanza una excepción.
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Devuelve el objeto User. Dado que nuestra entidad User implementa la interfaz UserDetails,
        // Spring Security puede utilizar directamente este objeto para la autenticación.
        return user;
    }
}
