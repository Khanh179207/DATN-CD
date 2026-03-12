package poly.edu.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
@ConditionalOnClass(Flyway.class)
@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FlywayRuntimeConfig {

    @Bean(name = "runtimeFlyway", initMethod = "migrate")
    @ConditionalOnMissingBean(Flyway.class)
    public Flyway runtimeFlyway(
            DataSource dataSource,
            @org.springframework.beans.factory.annotation.Value("${spring.flyway.locations:classpath:db/migration}") String locations,
            @org.springframework.beans.factory.annotation.Value("${spring.flyway.baseline-on-migrate:false}") boolean baselineOnMigrate,
            @org.springframework.beans.factory.annotation.Value("${spring.flyway.baseline-version:1}") String baselineVersion,
            @org.springframework.beans.factory.annotation.Value("${spring.flyway.validate-on-migrate:true}") boolean validateOnMigrate
    ) {
        String[] configuredLocations = Arrays.stream(locations.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toArray(String[]::new);

        return Flyway.configure()
                .dataSource(dataSource)
                .locations(configuredLocations)
                .baselineOnMigrate(baselineOnMigrate)
                .baselineVersion(MigrationVersion.fromVersion(baselineVersion))
                .validateOnMigrate(validateOnMigrate)
                .load();
    }

    @Bean
    public static BeanFactoryPostProcessor entityManagerFactoryDependsOnFlyway() {
        return beanFactory -> {
            if (!beanFactory.containsBeanDefinition("entityManagerFactory") || !beanFactory.containsBeanDefinition("runtimeFlyway")) {
                return;
            }

            BeanDefinition entityManagerFactory = beanFactory.getBeanDefinition("entityManagerFactory");
            Set<String> dependsOn = new LinkedHashSet<>();
            if (entityManagerFactory.getDependsOn() != null) {
                dependsOn.addAll(Arrays.asList(entityManagerFactory.getDependsOn()));
            }
            dependsOn.add("runtimeFlyway");
            entityManagerFactory.setDependsOn(dependsOn.toArray(String[]::new));
        };
    }
}