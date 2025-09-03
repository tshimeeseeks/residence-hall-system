package com.rhs.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {

})
class RhsBackendApplicationTests {

	@Test
	@Disabled("MongoDB connection test disabled - focusing on unit tests")
	void contextLoads() {
	}
}