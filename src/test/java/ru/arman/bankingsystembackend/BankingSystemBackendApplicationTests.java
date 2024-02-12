package ru.arman.bankingsystembackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainerConfiguration.class)
class BankingSystemBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
