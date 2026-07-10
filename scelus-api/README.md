# scelus-api

API REST do Sistema Scelus do TJMA — Spring Boot 4 + Java 21.

## Stack

- Spring Boot 4.0.5 + Java 21
- `br.jus.tjma.infra:tjma-infra-spring:0.0.1` (Nexus TJMA) — autenticação Sentinela SSO
- PostgreSQL (`postgresDS`) — schema `dba_scelus`, datasource principal de domínio
- Oracle (`sentinelaDS`) — apenas autenticação/grupos (`DBA_SENTINELA`)

## Rodando localmente

1. Copie `.env.example` para `.env` e ajuste as senhas.
2. `./run-local.sh` (Windows: ajuste o `JAVA_HOME` no script se necessário) ou:
   ```
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```
3. Swagger UI: http://localhost:8080/scelus-api/swagger-ui.html

## Estrutura

```
src/main/java/br/jus/tjma/scelus/
├── config/     # Datasources (Postgres + Sentinela), Jackson, OpenAPI — NÃO criar SecurityConfig
├── comum/      # AppException, EntidadeNaoEncontradaException, GlobalExceptionHandler, PageResult
└── <dominio>/  # Módulos de negócio do Scelus (a criar)
```

## Nexus TJMA

Este projeto consome `tjma-infra-spring` via `https://nexusrepo.tjma.jus.br/repository/maven-public/`
(grupo). Configure `~/.m2/settings.xml` com um server `tjma-public` (credenciais de leitura) — veja
`ci/settings.xml` para o formato usado no pipeline (via variáveis de ambiente
`TJMA_MAVEN_CREDS_USR`/`TJMA_MAVEN_CREDS_PWD`).
