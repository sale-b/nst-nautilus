package config;

import com.nautilus.config.LiqubaseHelpper;
import com.nautilus.config.PropertiesCache;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;

public class LiquibaseConfig implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static boolean started = false;


    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            DataSource dataSource = (DataSource) SpringContext.INSTANCE.getValue().getBean("dataSource");
            started = true;
            if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("spring.liquibase.enabled"))) {
                LiqubaseHelpper liquibase = new LiqubaseHelpper(dataSource);
                liquibase.update();
            }

        }
    }

    @Override
    public void close() {
        // Your "after all tests" logic goes here
    }
}