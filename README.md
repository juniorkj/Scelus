# Scelus — Sistema de Acompanhamento de Crimes e Medidas Protetivas (TJMA)

Monorepo com backend e frontend do Scelus.

| Módulo | Tecnologia | Descrição |
|--------|-----------|-----------|
| [`scelus-api/`](./scelus-api/README.md) | Spring Boot 4 + Java 21 | API REST — PostgreSQL + Oracle (Sentinela) |
| [`scelus-web/`](./scelus-web/README.md) | Angular 21 + `@tjma/angular-21` | Frontend SPA |

---

## Desenvolvimento local

```bash
# Backend (scelus-api/)
# 1. Copie o .env.dev para .env e ajuste se necessário
./run-local.sh
# → http://localhost:8080/scelus-api/swagger-ui.html

# Frontend (scelus-web/)
npm install && npm start
# → http://localhost:4200  (com proxy para localhost:8080)
```

---

## Deploy com Docker — Visão Geral

O sistema é implantado como dois containers Docker na rede `docker_tjma_apps`, acessíveis através do proxy Nginx compartilhado (`proxy-apps-tjma`, porta 8082).

```
Internet → Apache (TLS) → :8082 → proxy-apps-tjma (Nginx)
                                      ├── /scelus-api → scelus-api:8080
                                      └── /scelus/    → scelus-web:80
                                   (rede docker_tjma_apps)
```

### Imagens no Registry

```
registro-ops.tjma.jus.br/tjma/scelus/scelus-api   (Spring Boot)
registro-ops.tjma.jus.br/tjma/scelus/scelus-web   (Angular + Nginx)
```

---

## Fluxo Completo de Release e Deploy

### 🔑 Pré-requisitos (uma vez)

```bash
# Efetue login no registry do TJMA
docker login registro-ops.tjma.jus.br

# Exportar credenciais Maven (Nexus TJMA) para compilação local da API
export TJMA_MAVEN_CREDS_USR=tjma-aplicacao
export TJMA_MAVEN_CREDS_PWD=<senha_nexus_leitura>
```

### 1. Incrementar a Versão

| Módulo | Arquivo | Campo |
|---|---|---|
| **Backend** | `scelus-api/pom.xml` | `<version>X.Y.Z</version>` |
| **Frontend** | `scelus-web/package.json` | `"version": "X.Y.Z"` |

### 2. Geração e Push de Ambas as Imagens

Para compilar o código e gerar/publicar as duas imagens no Registry, execute o script automatizado **a partir da raiz do monorepo**:

```bash
bash scripts/build-and-push.sh X.Y.Z
```

#### O que este script faz sob o capô:

##### A. Backend (API)
1. Executa `mvn package -DskipTests -B` na pasta `scelus-api/` para gerar o JAR.
2. Faz o build da imagem Docker enviando as credenciais do Maven de forma segura via BuildKit `--secret`.
3. Adiciona as tags `X.Y.Z` e `latest` na imagem gerada.

##### B. Frontend (Web)
1. Executa `npm ci` na pasta `scelus-web/` para instalar as dependências de forma limpa.
2. Executa `npm run build:prod` (`ng build --configuration production`) para gerar o build estático otimizado. *(Nota: O build de produção também serve para DEV; o proxy do servidor resolve a rota de comunicação).*
3. Cria a imagem Docker local (copiando os arquivos compilados da pasta `dist/` para dentro de um servidor web Nginx Alpine).
4. Adiciona as tags `X.Y.Z` e `latest` na imagem gerada.

##### C. Push
Publica no registry do TJMA ambas as imagens:
- `registro-ops.tjma.jus.br/tjma/scelus/scelus-api` (versão e latest)
- `registro-ops.tjma.jus.br/tjma/scelus/scelus-web` (versão e latest)

---

### 3. Deploy no Servidor

Conecte-se via SSH no servidor de destino e execute:

```bash
# Na primeira vez no servidor: configure as senhas reais
cp variables.dev.env variables.env
nano variables.env

# Sempre que quiser implantar uma nova versão:
bash scripts/deploy.sh dev      # Para Desenvolvimento
bash scripts/deploy.sh homol    # Para Homologação
bash scripts/deploy.sh prod     # Para Produção
```

O `deploy.sh` fará o `git pull` na branch correspondente, o `docker compose pull` para baixar as imagens geradas no passo anterior, e o `docker compose up -d` para reiniciar os serviços com downtime mínimo.

---

## Verificação Pós-Deploy

# 1. Checar imagens enviadas ao Registry (se necessário)
# (Use --ssl-no-revoke no Windows para evitar o erro de CRL do certificado TJMA)
curl --ssl-no-revoke -u <usuario>:<senha> https://registro-ops.tjma.jus.br/v2/tjma/scelus/scelus-api/tags/list
curl --ssl-no-revoke -u <usuario>:<senha> https://registro-ops.tjma.jus.br/v2/tjma/scelus/scelus-web/tags/list

# 2. Containers rodando no servidor
docker ps | grep scelus

# 3. Logs da API e do Web
docker logs scelus-api --tail 50
docker logs scelus-web --tail 20

# 4. Saúde da API
docker exec scelus-api wget -qO- http://localhost:8080/scelus-api/actuator/health

# 5. Acesso externo (DEV)
# Frontend: https://sistemasd.tjma.jus.br/scelus/
# Swagger: https://sistemasd.tjma.jus.br/scelus-api/swagger-ui.html
```

---

## Rollback

Caso necessite reverter para uma versão anterior de emergência:

```bash
# Pare e remova os containers atuais
docker stop scelus-api scelus-web
docker rm   scelus-api scelus-web

# Suba a versão estável anterior (ex: 0.9.0)
docker run -d --name scelus-api --network docker_tjma_apps --env-file variables.env \
  registro-ops.tjma.jus.br/tjma/scelus/scelus-api:0.9.0

docker run -d --name scelus-web --network docker_tjma_apps \
  registro-ops.tjma.jus.br/tjma/scelus/scelus-web:0.9.0
```
