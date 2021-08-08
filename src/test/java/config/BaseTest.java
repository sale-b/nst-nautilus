package config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, LiquibaseConfig.class})
@ContextConfiguration(classes = {TestConfig.class})
public class BaseTest {
}
