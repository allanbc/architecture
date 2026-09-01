package br.com.treinamento.architecture.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class PostgresPropertyTestSupport {

    private static final PostgresTestContainer POSTGRES = PostgresTestContainer.startWithSchema();

    protected Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword());
    }
}
