package ru.yandex.practicum.mybankfront;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.mybankfront.config.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
class MyBankFrontAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
