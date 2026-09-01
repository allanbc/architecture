package br.com.treinamento.architecture.support;

import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Configuração centralizada do PostgreSQL usado pelos testes de integração.
 *
 * <p>Cada suíte recebe uma instância isolada para que migrations e dados de um
 * teste não interfiram em outra engine de testes.</p>
 */
public final class PostgresTestContainer extends PostgreSQLContainer {

    private static final String IMAGE = "postgres:15-alpine";
    private PostgresTestContainer() {
        super(IMAGE);
    }

    public static PostgresTestContainer startNew() {
        var postgres = new PostgresTestContainer();
        postgres.start();
        return postgres;
    }

    public static PostgresTestContainer startWithSchema() {
        var postgres = new PostgresTestContainer();
        postgres.withInitScript("db/migration/V1__create_tables.sql");
        postgres.start();
        return postgres;
    }
}
