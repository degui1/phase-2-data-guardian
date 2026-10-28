package com.api_application.phase_2_data_guardian.support;

import java.util.Locale;
import org.springframework.test.context.ActiveProfilesResolver;

/** Selects an isolated test database without activating development profiles. */
public class TestDatabaseProfilesResolver implements ActiveProfilesResolver {
    @Override
    public String[] resolve(Class<?> testClass) {
        String database = System.getProperty("test.database", "h2").trim().toLowerCase(Locale.ROOT);
        return switch (database) {
            case "h2" -> new String[]{"test"};
            case "mysql" -> new String[]{"test-mysql"};
            default -> throw new IllegalArgumentException("Unsupported test.database: " + database
                    + ". Use h2 or mysql.");
        };
    }
}
