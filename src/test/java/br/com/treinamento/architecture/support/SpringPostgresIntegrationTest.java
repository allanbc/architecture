package br.com.treinamento.architecture.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class SpringPostgresIntegrationTest {

    private static final PostgresTestContainer POSTGRES = PostgresTestContainer.startNew();

    @DynamicPropertySource
    protected static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
