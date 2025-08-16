package com.empacatalog;

import com.empacatalog.config.SpringSecurityAuditorAware; // Importa la clase del auditor
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Importa la anotación @Bean
import org.springframework.data.domain.AuditorAware; // Importa la interfaz AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Mantiene la auditoría de JPA habilitada
public class EmpacatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmpacatalogApplication.class, args);
	}

	/**
	 * Bean para que Spring sepa quién es el usuario auditor.
	 * Retorna una instancia de nuestra clase SpringSecurityAuditorAware.
	 */
	@Bean
	public AuditorAware<String> auditorAware() {
		return new SpringSecurityAuditorAware();
	}
}
