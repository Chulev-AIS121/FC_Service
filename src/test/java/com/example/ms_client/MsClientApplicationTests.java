package com.example.ms_client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MsClientApplicationTests {

	@Test
	void contextLoads() {
	}

}
