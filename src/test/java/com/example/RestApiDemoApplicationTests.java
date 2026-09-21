package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName ("Test de Carga del Contexto de Spring Boot")
class RestApiDemoApplicationTests {

	@Test  // Este Test es para verificar que el contexto de Spring se levanta correctamente, 
	void contextLoads() { 	// es decir, que no hay errores de configuración
	
	}

}
