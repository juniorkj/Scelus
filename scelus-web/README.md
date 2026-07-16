# scelus-web

Frontend do Sistema Scelus do TJMA — Angular 21 Standalone + `@tjma/angular-21`.

## Rodando localmente

```
npm install
npm start
```

A app sobe em `http://localhost:4200` com proxy para `http://localhost:8080` (veja `proxy.conf.json`),
assumindo o `scelus-api` rodando localmente.

## Nexus TJMA

A biblioteca `@tjma/angular-21` é consumida direto do Nexus TJMA
(`https://nexusrepo.tjma.jus.br/repository/npm-public/`, configurado em `.npmrc`).

## Estrutura

```
src/app/
├── core/          # Guards, interceptors
├── shared/        # Componentes/diretivas/pipes reutilizáveis do scelus-web
├── features/
│   ├── home/      # Página inicial
│   └── <dominio>/ # Módulos de negócio do Scelus (a criar)
├── app.routes.ts  # TjAuthTemplate como wrapper raiz
├── menu.ts        # Menu lateral (TJ_AUTH_TEMPLATE_CONFIGS)
└── app.config.ts  # tjMenuProvider() + ...tjProviders(environment)
```

## Checklist para novas features

- [ ] Standalone Component com `templateUrl`/`styleUrl` separados
- [ ] Lazy loading via `loadComponent` em `app.routes.ts`
- [ ] Item correspondente em `menu.ts` (com `objetoSentinela`/`permissoes`)
- [ ] `canActivate: [authGuard]` + `data: { objetoSentinela, permissoes }` na rota
- [ ] Estende `TjCrudBaseComponent` (telas de manutenção Lista/Formulário)
- [ ] Ícones Lucide em PascalCase

---

## Deploy com Docker

O deploy usa imagem Docker publicada no registry TJMA (`registro-ops.tjma.jus.br`) e orquestrada
pelo `docker-compose.yml` na **raiz do monorepo**. O processo tem duas etapas:
**build/push local** e **deploy no servidor** via script.

> Veja também: `scripts/build-and-push.sh` e `scripts/deploy.sh` na raiz do projeto.

### Pré-requisitos

| Ferramenta | Versão mínima | Observação |
|------------|--------------|------------|
| Node.js / npm | 22 | Acesso ao Nexus TJMA (`.npmrc` já configurado) |
| Docker | 24+ | `docker login registro-ops.tjma.jus.br` |

### Passo 1 — Instalar dependências e compilar

```bash
# Dentro de scelus-web/
npm ci
npm run build:prod
```

> O build de produção usa `baseHref="/scelus/"` (configurado em `angular.json`) para
> que o Angular Router funcione corretamente atrás do proxy Nginx que remove o prefixo
> antes de repassar ao container.

### Passo 2 — Build da imagem Docker

```bash
# Dentro de scelus-web/
docker build \
  -t registro-ops.tjma.jus.br/tjma/scelus/scelus-web:1.0.0 \
  -t registro-ops.tjma.jus.br/tjma/scelus/scelus-web:latest \
  .
```

> O `Dockerfile` usa **multi-stage**: Stage 1 compila com Node 22 Alpine; Stage 2 serve
> os arquivos estáticos via Nginx Alpine. A imagem final tem ~30 MB.

### Passo 3 — Push para o registry

```bash
docker push registro-ops.tjma.jus.br/tjma/scelus/scelus-web:1.0.0
docker push registro-ops.tjma.jus.br/tjma/scelus/scelus-web:latest
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
export TJMA_MAVEN_CREDS_USR=seu_usuario
export TJMA_MAVEN_CREDS_PWD=sua_senha
docker login registro-ops.tjma.jus.br

# Versão como argumento (padrão: latest)
bash scripts/build-and-push.sh 1.0.0
```

---

### Incrementando a release

1. Atualize `"version"` no `package.json` (ex: `"1.1.0"`)
2. Commit e push na branch `main` (ou `develop` para DEV)
3. Build e push:
   ```bash
   bash scripts/build-and-push.sh 1.1.0
   ```
4. Deploy no servidor:
   ```bash
   bash scripts/deploy.sh dev
   ```

> Se quiser fixar a versão no `docker-compose.yml` da raiz (em vez de `:latest`), altere
> o campo `image:` do serviço `scelus-web` para a versão desejada antes do deploy.

---

### Roteamento em produção

O Nginx do proxy (`proxy-apps-tjma`) roteia:

| Caminho externo | Container de destino |
|-----------------|---------------------|
| `/scelus/` | `scelus-web:80` (rewrite: remove `/scelus`) |
| `/scelus-api/` | `scelus-api:8080` |

O Nginx **interno** do container `scelus-web` também faz proxy de `/scelus-api/` →
`scelus-api:8080` (via rede Docker interna), servindo como fallback direto.

---

### Health check / verificação

```bash
# Frontend acessível
curl -I https://sistemasd.tjma.jus.br/scelus/

# Logs do container
docker logs scelus-web --tail 30
```

---

### Rollback

```bash
# Subir versão anterior sem redeploy completo
docker stop scelus-web && docker rm scelus-web
docker run -d \
  --name scelus-web \
  --network docker_tjma_apps \
  registro-ops.tjma.jus.br/tjma/scelus/scelus-web:0.9.0
```

