# Comandos Docker do projeto

Este guia reúne os comandos mais usados para construir, executar e limpar os recursos Docker da aplicação `architecture`.

Execute os comandos do Compose na raiz do projeto, onde está o arquivo `compose.yaml`.

## Conceitos rápidos

| Recurso | Descrição |
| --- | --- |
| Imagem | Pacote imutável usado como base para criar contêineres |
| Contêiner | Instância em execução ou parada de uma imagem |
| Volume | Armazenamento persistente, usado neste projeto pelo PostgreSQL |
| Rede | Permite que os contêineres se comuniquem por nome de serviço |
| Cache de build | Camadas reutilizadas pelo Docker para acelerar builds futuros |

## Desenvolvimento pela IDE

### Subir somente o PostgreSQL

```powershell
docker compose up -d postgres
```

A aplicação executada pela IDE acessa o banco por:

```text
Host: 127.0.0.1
Porta: 5433
Database: db_archi
Usuário: postgres
Senha: postgres
```

### Verificar o banco

```powershell
docker compose ps postgres
docker compose logs -f postgres
```

### Parar somente o banco

```powershell
docker compose stop postgres
```

### Iniciar novamente o banco existente

```powershell
docker compose start postgres
```

## Construção da imagem

### Construir uma imagem manualmente

```powershell
docker build -t architecture-api:dev .
```

Esse comando:

1. Lê o `Dockerfile` da pasta atual.
2. Compila a aplicação.
3. Gera uma imagem chamada `architecture-api`.
4. Aplica a tag `dev` à imagem.

Ele não inicia um contêiner e não sobe o PostgreSQL.

Confira a imagem criada:

```powershell
docker image ls architecture-api
```

Outros exemplos de tags:

```powershell
docker build -t architecture-api:latest .
docker build -t architecture-api:1.0.0 .
```

## Execução completa pelo Compose

### Construir e subir banco e aplicação

```powershell
docker compose up -d --build
```

O Compose executa o seguinte fluxo:

```text
constrói a imagem → inicia o PostgreSQL → aguarda o healthcheck → inicia a API
```

A API fica disponível em:

```text
http://localhost:8080
```

### Subir sem reconstruir a imagem

```powershell
docker compose up -d
```

Use quando a imagem já foi construída e o código não mudou.

### Reconstruir e subir somente o serviço da aplicação

```powershell
docker compose up -d --build app
```

Como `app` depende de `postgres`, o Compose também inicia o banco caso ele esteja parado.

### Subir somente a aplicação, ignorando dependências

```powershell
docker compose up -d --no-deps app
```

Use com cuidado: a aplicação falhará se não houver um PostgreSQL acessível em `postgres:5432` na rede do Compose.

## Executar uma imagem manualmente

Primeiro suba o banco e deixe o Compose criar a rede:

```powershell
docker compose up -d postgres
```

Depois execute a imagem na mesma rede:

```powershell
docker run --rm `
  --name architecture-api `
  --network architecture_default `
  -p 8080:8080 `
  -e DB_HOST=postgres `
  -e DB_PORT=5432 `
  -e DB_NAME=db_archi `
  -e DB_USERNAME=postgres `
  -e DB_PASSWORD=postgres `
  -e JPA_DDL_AUTO=update `
  architecture-api:dev
```

| Opção | Finalidade |
| --- | --- |
| `--rm` | Remove o contêiner automaticamente quando ele parar |
| `--name` | Evita nomes aleatórios e define `architecture-api` |
| `--network` | Coloca a API na mesma rede do PostgreSQL |
| `-p 8080:8080` | Publica a API na porta `8080` do computador |
| `-e` | Define uma variável de ambiente no contêiner |

## Estado e logs

### Listar serviços deste projeto

```powershell
docker compose ps
```

### Listar todos os contêineres da máquina

```powershell
docker ps -a
```

### Acompanhar logs da aplicação

```powershell
docker compose logs -f app
```

### Acompanhar logs do PostgreSQL

```powershell
docker compose logs -f postgres
```

### Acompanhar todos os logs

```powershell
docker compose logs -f
```

## Parar e remover recursos do projeto

### Parar os serviços sem removê-los

```powershell
docker compose stop
```

### Iniciar novamente os serviços parados

```powershell
docker compose start
```

### Remover contêineres e rede, preservando o banco

```powershell
docker compose down --remove-orphans
```

O volume `postgres_archi_data` é preservado.

### Remover contêineres, rede e dados do banco

```powershell
docker compose down -v --remove-orphans
```

> Atenção: `-v` apaga o volume do PostgreSQL e os dados não poderão ser recuperados pelo Docker.

## Limpeza de contêineres

### Remover todos os contêineres parados

```powershell
docker container prune -f
```

Esse comando remove contêineres com status `Exited` ou `Created`, inclusive de outros projetos. Ele ajuda a eliminar contêineres antigos com nomes aleatórios.

Ele não remove contêineres em execução, imagens ou volumes.

### Remover absolutamente todos os contêineres

No PowerShell:

```powershell
docker ps -aq | ForEach-Object { docker rm -f $_ }
```

> Atenção: esse comando força a remoção de contêineres parados e em execução de todos os projetos Docker da máquina.

## Limpeza de imagens

### Remover apenas imagens não utilizadas

```powershell
docker image prune -a -f
```

Remove imagens que não são usadas por nenhum contêiner. Imagens vinculadas a contêineres existentes são preservadas.

### Remover todas as imagens da máquina

No PowerShell:

```powershell
docker image ls -aq | Sort-Object -Unique | ForEach-Object { docker image rm -f $_ }
```

> Atenção: esse comando força a remoção de todas as imagens de todos os projetos. Elas precisarão ser baixadas ou reconstruídas novamente.

### Conferir as imagens restantes

```powershell
docker image ls
```

## Limpeza geral

### Remover recursos não utilizados e cache de build

```powershell
docker system prune -a -f
```

Esse comando remove globalmente:

- todos os contêineres parados;
- todas as imagens não utilizadas;
- redes não utilizadas;
- cache de build não utilizado.

Por padrão, volumes não são removidos.

### Remover também volumes não utilizados

```powershell
docker system prune -a --volumes -f
```

> Atenção: `--volumes` pode apagar bancos de dados e outros dados persistentes de qualquer projeto. Use somente quando tiver certeza de que nenhum volume precisa ser preservado.

### Limpar somente o cache de build

```powershell
docker builder prune -a -f
```

## Resumo dos comandos mais usados

| Objetivo | Comando |
| --- | --- |
| Subir somente o banco | `docker compose up -d postgres` |
| Construir uma imagem `dev` | `docker build -t architecture-api:dev .` |
| Subir tudo reconstruindo | `docker compose up -d --build` |
| Subir tudo sem reconstruir | `docker compose up -d` |
| Ver os serviços | `docker compose ps` |
| Ver todos os logs | `docker compose logs -f` |
| Parar temporariamente | `docker compose stop` |
| Remover o projeto e preservar dados | `docker compose down --remove-orphans` |
| Remover contêineres parados | `docker container prune -f` |
| Remover imagens não utilizadas | `docker image prune -a -f` |
| Remover todas as imagens | `docker image ls -aq \| Sort-Object -Unique \| ForEach-Object { docker image rm -f $_ }` |
| Limpar contêineres, imagens, redes e cache | `docker system prune -a -f` |
