# Architecture — gerenciamento de pedidos

Os principais comandos de construção, execução e limpeza estão reunidos em [DOCKER-COMMANDS.md](DOCKER-COMMANDS.md).

API REST de exemplo para gerenciamento de pedidos, construída com Java 21 e Spring Boot. O projeto segue uma organização inspirada em arquitetura hexagonal, separando domínio, casos de uso e adaptadores de entrada e saída.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Gradle Wrapper 9.7.1
- Spring Web MVC e Bean Validation
- Spring Data JPA
- PostgreSQL 15
- Flyway, atualmente desativado
- MapStruct e Lombok
- Kafka e Resilience4j, disponíveis como dependências para evolução da aplicação
- JUnit, Mockito e Jqwik para testes
- Docker e Docker Compose

## Estrutura do projeto

```text
src/main/java/br/com/treinamento/architecture
├── application
│   ├── order/ports/in       # Casos de uso expostos pela aplicação
│   ├── order/ports/out      # Portas de persistência e mensageria
│   └── service              # Implementação dos casos de uso
├── domain
│   ├── order                # Entidades e regras do domínio
│   ├── validation           # Validações do domínio
│   └── exception            # Exceções e tratamento de erros
└── infrastructure/adapters
    ├── in/web               # Controller, DTOs e mapper HTTP
    └── out                  # Persistência e mensageria
```

O fluxo principal é:

```text
HTTP → OrderController → OrderUseCase/OrderService → OrderRepository → PostgreSQL
```

## Pré-requisitos

Para executar pela IDE ou pelo Gradle:

- JDK 21 configurado;
- Docker com Docker Compose para o PostgreSQL;
- portas `5433` e `8080` disponíveis.

Não é necessário instalar o Gradle, pois o repositório contém o Gradle Wrapper.

Para executar tudo com contêineres, basta ter Docker e Docker Compose. A imagem da aplicação é compilada em um estágio com JDK 21 e executada em outro estágio menor, somente com o JRE 21.

## Configuração da aplicação

A configuração padrão está em `src/main/resources/application.properties`:

| Variável | Valor padrão | Uso |
| --- | --- | --- |
| `DB_HOST` | `127.0.0.1` | Host do PostgreSQL |
| `DB_PORT` | `5433` | Porta do PostgreSQL |
| `DB_NAME` | `db_archi` | Nome do banco |
| `DB_USERNAME` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `postgres` | Senha do banco |
| `JPA_DDL_AUTO` | `none` | Estratégia de criação do schema pelo Hibernate |

Quando a aplicação é executada pela IDE, os valores padrão apontam para `127.0.0.1:5433`. No Compose, o serviço `app` sobrescreve `DB_HOST` e `DB_PORT` com `postgres:5432`, que é o endereço interno do serviço de banco na rede Docker.

O arquivo `.env.example` contém todas as variáveis aceitas pelo Compose. Copie-o para `.env` se quiser personalizar os valores. O arquivo `.env` não deve ser versionado.

> O projeto ainda não possui migrations Flyway. O Compose usa `JPA_DDL_AUTO=update` por padrão para criar as tabelas em desenvolvimento. Em produção, prefira migrations versionadas.

## Subindo somente o banco com Docker Compose

Na raiz do projeto, execute:

```bash
docker compose up -d postgres
```

O Compose criará:

- contêiner `postgres-archi`;
- banco `db_archi`;
- usuário e senha `postgres`;
- porta `5433` no host, encaminhada para `5432` no contêiner;
- volume persistente `postgres_archi_data`.

O healthcheck usa `pg_isready`, portanto o banco só é considerado saudável depois de aceitar conexões.

Confira o estado e os logs:

```bash
docker compose ps
docker compose logs -f postgres
```

Para parar o banco preservando os dados:

```bash
docker compose down
```

Para parar e apagar também o volume do banco:

```bash
docker compose down -v
```

O último comando remove permanentemente os dados armazenados no volume.

## Executando pela IDE

1. Suba o PostgreSQL:

   ```bash
   docker compose up -d postgres
   ```

2. Importe o projeto Gradle na IDE e selecione o JDK 21.
3. Crie uma configuração de execução para a classe:

   ```text
   br.com.treinamento.architecture.ArchitectureApplication
   ```

4. Como ainda não existem migrations, adicione esta variável de ambiente à configuração da IDE:

   ```text
   JPA_DDL_AUTO=update
   ```

5. Execute a aplicação. A API ficará disponível em `http://localhost:8080`.

Caso o schema já seja administrado externamente, omita `JPA_DDL_AUTO`.

## Executando pelo Gradle

Com o banco já iniciado, no Windows PowerShell:

```powershell
$env:JPA_DDL_AUTO = "update"
.\gradlew.bat bootRun
```

Em Linux ou macOS:

```bash
JPA_DDL_AUTO=update ./gradlew bootRun
```

## Subindo banco e aplicação juntos

O `compose.yaml` também constrói a imagem pelo Dockerfile. Para subir o banco e a API juntos:

```bash
docker compose up -d --build
```

O fluxo é:

```text
postgres inicia → healthcheck fica saudável → aplicação inicia → conecta em postgres:5432
```

Verifique os serviços e acompanhe os logs:

```bash
docker compose ps
docker compose logs -f app
```

Para parar os serviços preservando o banco:

```bash
docker compose down
```

## Conexão pelo DBeaver

Crie uma conexão PostgreSQL no DBeaver com:

| Campo | Valor |
| --- | --- |
| Host | `localhost` |
| Porta | `5433` |
| Database | `db_archi` |
| Usuário | `postgres` |
| Senha | `postgres` |

Se os valores forem alterados no `.env`, use os mesmos no DBeaver. A porta do DBeaver é `DB_HOST_PORT`; a porta `5432` é usada apenas entre os contêineres.

## Endpoints disponíveis

### Swagger e OpenAPI

Com a aplicação em execução, a documentação interativa e a especificação OpenAPI ficam disponíveis em:

```text
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
```

### Criar pedido

```http
POST /orders
Content-Type: application/json
```

Exemplo de corpo:

```json
{
  "customerId": "696b3724-e2aa-4494-9f3f-b5bf36c4c205",
  "items": [
    {
      "sku": "SKU-1",
      "quantity": 2,
      "unitPrice": 10.50
    }
  ]
}
```

Exemplo com cURL:

```bash
curl -i -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"696b3724-e2aa-4494-9f3f-b5bf36c4c205","items":[{"sku":"SKU-1","quantity":2,"unitPrice":10.50}]}'
```

Uma criação bem-sucedida retorna HTTP `201 Created` e o cabeçalho `Location` com a URL do pedido.

### Buscar pedido por ID

```http
GET /orders/{id}
```

Exemplo:

```bash
curl http://localhost:8080/orders/4ae6a669-6144-4c2a-8148-bf8b8ee77df7
```

## Testes unitários

Execute toda a suíte:

No Windows:

```powershell
.\gradlew.bat test
```

Em Linux ou macOS:

```bash
./gradlew test
```

O relatório HTML fica disponível em:

```text
build/reports/tests/test/index.html
```

Os testes atuais são isolados e usam JUnit, Mockito, testes parametrizados e propriedades Jqwik. Testes integrados com PostgreSQL e Kafka poderão ser adicionados posteriormente com Testcontainers.

## Solução de problemas

### A porta 5433 já está em uso

Altere o lado esquerdo do mapeamento em `compose.yaml` e ajuste `spring.datasource.url`. Por exemplo, para usar a porta `5434`:

```yaml
ports:
  - "5434:5432"
```

```text
jdbc:postgresql://127.0.0.1:5434/db_archi
```

### A aplicação não encontra o banco quando executada no Docker

Dentro do contêiner, `127.0.0.1` aponta para o próprio contêiner da aplicação. No Compose, confirme que `DB_HOST=postgres` e `DB_PORT=5432`. O nome `postgres` é resolvido pelo DNS interno da rede Docker.

### Erro indicando que uma tabela não existe

O Flyway está desativado e ainda não existem migrations. Para desenvolvimento, execute com:

```text
JPA_DDL_AUTO=update
```

Para produção, crie migrations Flyway e habilite `spring.flyway.enabled=true` em vez de depender do Hibernate para alterar o schema.

### O Gradle não encontra o Java

Confirme que a IDE usa JDK 21 e que `JAVA_HOME` aponta para uma instalação compatível:

```bash
java -version
```
