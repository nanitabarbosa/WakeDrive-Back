package com.wakedrive.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.password=postgres",
		"app.jwt.secret=clave-solo-para-pruebas-clave-solo-para-pruebas",
		"app.device.api-key=llave-solo-para-pruebas",
		"app.seed.super-admin-email=admin-pruebas@example.com",
		"app.seed.super-admin-password=clave-solo-para-pruebas"
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
