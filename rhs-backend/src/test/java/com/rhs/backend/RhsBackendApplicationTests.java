package com.rhs.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@ActiveProfiles("test")
@DirtiesContext
class RhsBackendApplicationTests {

	@Test
	void contextLoads() {
	}
}