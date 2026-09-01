package br.com.treinamento.architecture.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.UUID;

import br.com.treinamento.architecture.support.PostgresPropertyTestSupport;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.BigRange;
import net.jqwik.api.constraints.Scale;

@Label("PostgreSQL - propriedades de persistência de pedidos")
class OrderDatabasePropertyTest extends PostgresPropertyTestSupport {

    /**
     * Testa a persistência de pedidos com totais monetários não negativos no banco de dados PostgreSQL.
     *
     * <p>Este teste utiliza a biblioteca jqwik para gerar valores aleatórios de totais monetários
     * dentro do intervalo especificado (0.00 a 999999.99) e verifica se cada total é preservado
     * corretamente após a inserção no banco de dados.</p>
     *
     * <p>O teste realiza uma inserção de pedido com o total gerado e, em seguida, realiza uma
     * consulta para recuperar o total armazenado, comparando-o com o valor original.</p>
     *
     * <p>O teste é executado várias vezes (30 tentativas) para garantir que a propriedade seja
     * válida para diferentes valores de entrada.</p>
     *
     * @param total O total monetário gerado aleatoriamente para o pedido.
     * @throws Exception Se ocorrer algum erro durante a execução do teste.
     */
    @Property(tries = 30)
    @Label("Todo total monetário não negativo preserva o valor no PostgreSQL")
    void shouldPersistEveryNonNegativeTotal(
        @ForAll  
        @BigRange(min = "0.00", max = "999999.99")
        @Scale(2)
        BigDecimal total) throws Exception {
        
            UUID orderId = UUID.randomUUID();

            try (var connection = openConnection();
                PreparedStatement insert = connection.prepareStatement("""
                        INSERT INTO orders (id, custumer_id, status, total, created_at)
                        VALUES (?, ?, 'CREATED', ?, CURRENT_TIMESTAMP)
                        """);
                PreparedStatement select = connection.prepareStatement(
                        "SELECT total FROM orders WHERE id = ?")) {
                insert.setObject(1, orderId);
                insert.setObject(2, UUID.randomUUID());
                insert.setBigDecimal(3, total);
                insert.executeUpdate();

                select.setObject(1, orderId);
                try (var result = select.executeQuery()) {
                    result.next();
                    assertEquals(0, total.compareTo(result.getBigDecimal("total")));
                }
            }
    }

}
