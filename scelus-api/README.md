# scelus-api

API REST do Sistema Scelus do TJMA — Spring Boot 4 + Java 21.

## Stack

- Spring Boot 4.0.5 + Java 21
- `br.jus.tjma.infra:tjma-infra-spring:0.0.1` (Nexus TJMA) — autenticação Sentinela SSO
- PostgreSQL (`postgresDS`) — schema `dba_scelus`, datasource principal de domínio
- Oracle (`sentinelaDS`) — apenas autenticação/grupos (`DBA_SENTINELA`)

## Rodando localmente

1. **Dentro de `scelus-api/`**, copie `.env.dev` para `.env` e ajuste se necessário:

   ```
   POSTGRES_DB_PASSWORD=dba_scelus
   SENTINELA_DB_PASSWORD=dba_sentinela
   TJMA_AMBIENTE=DESENV
   SPRING_PROFILES_ACTIVE=desenv
   TJMA_SENTINELA_URL=https://sistemasd.tjma.jus.br/sentinela
   ```

2. Execute **dentro de `scelus-api/`**:

   ```bash
   # Linux/Mac
   ./run-local.sh

   # Windows / manual
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

3. Swagger UI: http://localhost:8080/scelus-api/swagger-ui.html

## Estrutura

```
src/main/java/br/jus/tjma/scelus/
├── config/       # Datasources (Postgres + Sentinela), Jackson, OpenAPI — NÃO criar SecurityConfig
├── comum/        # AppException, EntidadeNaoEncontradaException, GlobalExceptionHandler
├── changelog/    # Changelog (GET /api/changelog, tb_change_log)
└── dominio/
    ├── controller/   # CrimeController, MpuController, DominioController, ProcessoPjeController
    ├── dto/          # Records Java 21 de request/response/filtro
    ├── model/        # Entidades JPA
    ├── repository/   # Spring Data JPA repositories
    └── service/      # CrimeService, CadastroCrimeCompletoService, MpuService, etc.
```

## Nexus TJMA

Este projeto consome `tjma-infra-spring` via `https://nexusrepo.tjma.jus.br/repository/maven-public/`.

**Build local** — use o `~/.m2/settings.xml` (configurado uma vez, sem variáveis de ambiente):

```xml
<server>
  <id>tjma-public</id>
  <username>tjma-aplicacao</username>
  <password><!-- senha de leitura --></password>
</server>
```

**Build de CI/CD (pipelines)** — o arquivo `ci/settings.xml` presente no projeto injeta as
credenciais via variáveis de ambiente `TJMA_MAVEN_CREDS_USR` / `TJMA_MAVEN_CREDS_PWD`.
Esse arquivo existe para uso **exclusivo dos pipelines** — não use `-s ci/settings.xml`
em desenvolvimento local (o Maven já usa o `.m2` global por padrão).

---

## Deploy com Docker

O deploy usa imagem Docker publicada no registry TJMA (`registro-ops.tjma.jus.br`) e
orquestrada pelo `docker-compose.yml` na **raiz do monorepo**.

> Veja também: `scripts/build-and-push.sh` e `scripts/deploy.sh` na raiz do projeto.

### Pré-requisitos

| Ferramenta | Versão mínima | Observação |
|------------|--------------|------------|
| Java / Maven | 21 / 3.9 | `~/.m2/settings.xml` com acesso ao Nexus TJMA |
| Docker + BuildKit | 24+ | `DOCKER_BUILDKIT=1` habilitado |
| Acesso ao registry | — | `docker login registro-ops.tjma.jus.br` |

### Passo 1 — Compilar o JAR

> Execute **dentro de `scelus-api/`**:

```bash
mvn package -DskipTests -B
```

O Maven usa automaticamente o `~/.m2/settings.xml`. O `-s ci/settings.xml` é usado apenas
pelo script `build-and-push.sh` e pipelines de CI/CD.

### Passo 2 — Build da imagem Docker

> Execute **dentro de `scelus-api/`**.
> O `--secret` injeta as credenciais do Nexus no build sem gravá-las na imagem.

```bash
# Exportar as credenciais de leitura do Nexus TJMA
export TJMA_MAVEN_CREDS_USR=tjma-aplicacao
export TJMA_MAVEN_CREDS_PWD=<senha_nexus_leitura>

DOCKER_BUILDKIT=1 docker build \
  --secret id=maven_settings,src=ci/settings.xml \
  -t registro-ops.tjma.jus.br/tjma/scelus/scelus-api:1.0.0 \
  -t registro-ops.tjma.jus.br/tjma/scelus/scelus-api:latest \
  .
```

> O `Dockerfile` usa **multi-stage**: Stage 1 compila com Maven (imagem descartada);
> Stage 2 copia apenas o JAR para uma imagem JRE Alpine final (~150 MB).
> A tag `latest` é convenção do `deploy.sh` para sempre subir a versão mais recente.

### Passo 3 — Push para o registry

```bash
docker push registro-ops.tjma.jus.br/tjma/scelus/scelus-api:1.0.0
docker push registro-ops.tjma.jus.br/tjma/scelus/scelus-api:latest
```

### Passo 4 — Deploy no servidor

```bash
# No servidor (primeira vez: crie o variables.env com senhas reais)
cp variables.dev.env variables.env && nano variables.env

# Deploy
bash scripts/deploy.sh dev        # ou: homol | prod
```

O `deploy.sh` executa: `git pull` → `docker compose pull` → `docker compose up -d` →
`docker image prune -f`.

---

### Script automatizado (build + push em um comando)

Execute da **raiz do monorepo** para gerar e publicar ambas as imagens (API + Web):

```bash
export TJMA_MAVEN_CREDS_USR=tjma-aplicacao
export TJMA_MAVEN_CREDS_PWD=<senha_nexus_leitura>
docker login registro-ops.tjma.jus.br

# Versão como argumento (padrão: latest)
bash scripts/build-and-push.sh 1.0.0
```

---

### Incrementando a release

1. Atualize `<version>` no `pom.xml` (ex: `1.1.0`)
2. Commit e push na branch `main`
3. Build e push:
   ```bash
   bash scripts/build-and-push.sh 1.1.0
   ```
4. Deploy no servidor:
   ```bash
   bash scripts/deploy.sh dev
   ```

> Se quiser fixar a versão no `docker-compose.yml` da raiz (em vez de `:latest`), altere
> o campo `image:` do serviço `scelus-api` para a versão desejada antes do deploy.

---

### Variáveis de ambiente

O container lê o arquivo `variables.env` na raiz do projeto (gerado no servidor a partir
do template `variables.dev.env` — nunca versionado com senhas reais).

| Variável | Descrição |
|----------|-----------|
| `SPRING_PROFILES_ACTIVE` | Perfil Spring: `local` / `desenv` / `homol` / `prod` |
| `TJMA_AMBIENTE` | Label de ambiente: `DESENV-LOCAL` / `DESENV` / `HOMOL` / `PROD` |
| `POSTGRES_DB_PASSWORD` | Senha do schema `dba_scelus` |
| `SENTINELA_DB_PASSWORD` | Senha do schema Sentinela |
| `TJMA_SENTINELA_URL` | URL base do Sentinela |

---

### Health check / Verificação do Registry

```bash
# Verificar imagens enviadas ao Registry
# (Use --ssl-no-revoke no Windows para contornar erro de CRL do certificado TJMA)
curl --ssl-no-revoke -u <usuario>:<senha> https://registro-ops.tjma.jus.br/v2/tjma/scelus/scelus-api/tags/list

# Teste de saúde via proxy (DEV)
curl https://sistemasd.tjma.jus.br/scelus-api/actuator/health

# Direto no container
docker exec scelus-api wget -qO- http://localhost:8080/scelus-api/actuator/health
```

---

### Rollback

```bash
# Subir versão anterior sem redeploy completo
docker stop scelus-api && docker rm scelus-api
docker run -d \
  --name scelus-api \
  --network docker_tjma_apps \
  --env-file variables.env \
  registro-ops.tjma.jus.br/tjma/scelus/scelus-api:0.9.0
```
