package config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public enum SpringContext {

    INSTANCE;

    private final ApplicationContext value = new AnnotationConfigApplicationContext(TestConfig.class);

    public ApplicationContext getValue() {
        return value;
    }

}
