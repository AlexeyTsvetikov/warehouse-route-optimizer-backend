package ru.tsvetikov.warehouse.router.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tsvetikov.warehouse.router.aspect.SqlQueryCounter;

@Configuration
@RequiredArgsConstructor
public class HibernateInspectorConfig {

    private final SqlQueryCounter sqlQueryCounter;

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return properties -> properties.put(AvailableSettings.STATEMENT_INSPECTOR, sqlQueryCounter);
    }
}
