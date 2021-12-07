package common.configs;

import common.configs.annotations.ConfigurationProperties;
import common.configs.annotations.Value;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@ConfigurationProperties
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(staticName = "apply")
public class ApplicationConfiguration {

    @Value("database")
    DatabaseConfiguration database;

    public static ApplicationConfiguration apply() {
        return Configs.mapToConfigClass(ApplicationConfiguration.class, "application");
    }

}
