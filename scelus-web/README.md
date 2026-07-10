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
