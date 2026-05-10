package com.fiap.tech_challenge_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class TechChallengeBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
