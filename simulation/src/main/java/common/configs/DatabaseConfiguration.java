package common.configs;

import common.configs.annotations.ConfigurationProperties;
import common.configs.annotations.Value;
import lombok.*;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@ConfigurationProperties
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(staticName = "apply")
public class DatabaseConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Value("connection")
    String connection;

    @Value("username")
    String username;

    @Value("password")
    String password;

    @Value("enable-migration")
    boolean enableMigration;

    public static DatabaseConfiguration apply(String connection, String username, String password) {
        return apply(connection, username, password, false);
    }

    public void migrate() {
        if (enableMigration) {
            var flyway = Flyway.configure().dataSource(connection, username, password).load();
            var result = flyway.migrate();
            LOG.info("Migrated `{}`, {}", connection, result);
        } else {
            LOG.info("Skipping migration (disabled).");
        }
    }

}
