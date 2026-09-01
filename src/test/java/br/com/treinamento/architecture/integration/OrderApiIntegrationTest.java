package br.com.treinamento.architecture.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.treinamento.architecture.support.SpringPostgresIntegrationTest;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@WithMockUser
class OrderApiIntegrationTest extends SpringPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static String createdOrderLocation;
    
    /**
     * Testa a criação de um pedido e a persistência no banco de dados PostgreSQL.
     *
     * <p>Este teste realiza uma requisição POST para criar um pedido válido e
     * verifica se o pedido foi criado com sucesso, retornando o status HTTP 201
     * (Created) e o cabeçalho "Location" contendo a URL do recurso criado.</p>
     *
     * <p>Em seguida, realiza uma requisição GET para recuperar o pedido criado e
     * verifica se os dados retornados correspondem aos esperados.</p>
     *
     * <p>O teste é executado em duas etapas, garantindo que a criação e a
     * recuperação do pedido funcionem corretamente.</p>
     */
    @Test
    @Order(1)
    void shouldCreateAndPersistOrder() throws Exception {
        createdOrderLocation = mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderJson()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.custumerId").value("696b3724-e2aa-4494-9f3f-b5bf36c4c205"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.total").value(21.0))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    /**
     * Testa a recuperação de um pedido previamente criado no banco de dados
     * PostgreSQL.
     *
     * <p>Este teste realiza uma requisição GET para recuperar o pedido criado na
     * etapa anterior e verifica se os dados retornados correspondem aos
     * esperados.</p>
     *
     * <p>O teste é executado após a criação do pedido, garantindo que a
     * recuperação funcione corretamente.</p>
     */
    @Test
    @Order(2)
    void shouldRetrieveOrderFromPostgres() throws Exception {
        mockMvc.perform(get(createdOrderLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.custumerId").value("696b3724-e2aa-4494-9f3f-b5bf36c4c205"))
                .andExpect(jsonPath("$.items[0].sku").value("SKU-1"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    /**
     * Testa a validação de pedidos inválidos.
     *
     * <p>Este teste utiliza o recurso de testes parametrizados do JUnit 5 para
     * verificar diferentes cenários de pedidos inválidos. Para cada cenário,
     * realiza uma requisição POST com um payload inválido e verifica se o
     * status HTTP retornado é 400 (Bad Request) e se o campo específico que
     * causou a falha de validação está presente na resposta.</p>
     *
     * <p>Os cenários testados incluem:</p>
     * <ul>
     *   <li>Cliente ausente</li>
     *   <li>Lista de itens vazia</li>
     *   <li>Quantidade inválida (menor ou igual a zero)</li>
     * </ul>
     *
     * @param scenario Descrição do cenário de teste
     * @param payload Payload JSON do pedido inválido
     * @param field Campo específico que causou a falha de validação
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidOrders")
    @Order(3)
    void shouldRejectInvalidOrders(String scenario, String payload, String field) throws Exception {
        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['fields']['" + field + "']").exists());
    }

    static Stream<Arguments> invalidOrders() {
        return Stream.of(
                Arguments.of("cliente ausente",
                        """
                        {"items":[{"sku":"SKU-1","quantity":2,"unitPrice":10.50}]}
                        """, "customerId"),
                Arguments.of("lista de itens vazia",
                        """
                        {"customerId":"696b3724-e2aa-4494-9f3f-b5bf36c4c205","items":[]}
                        """, "items"),
                Arguments.of("quantidade inválida",
                        """
                        {"customerId":"696b3724-e2aa-4494-9f3f-b5bf36c4c205","items":[{"sku":"SKU-1","quantity":0,"unitPrice":10.50}]}
                        """, "items[0].quantity"));
    }

    private static String validOrderJson() {
        return """
                {
                  "customerId":"696b3724-e2aa-4494-9f3f-b5bf36c4c205",
                  "items":[{"sku":"SKU-1","quantity":2,"unitPrice":10.50}]
                }
                """;
    }
}
