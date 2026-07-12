# Scelus — Contexto do Projeto (TJMA)

Sistema de acompanhamento de crimes, processos e medidas protetivas (MPU) do TJMA.
Monorepo: `scelus-api` (Spring Boot 4 + Java 21, PostgreSQL `dba_scelus` + Oracle Sentinela) e `scelus-web` (Angular 21 + `@tjma/angular-21`).

## Regras obrigatórias (leia antes de codificar)

@.agents/rules/code-rules.md
@.agents/rules/design-rules.md

## Skills de referência da infraestrutura TJMA

- Backend (Sentinela, procedures, build/Nexus): `.agents/skills/tjma-infra-spring/SKILL.md`
- Frontend (componentes, CRUD, layout/CSS, botões): `.agents/skills/tjma-angular-ui/SKILL.md`

## Resumo das convenções inegociáveis

1. **Priorizar funções do banco** (`pkg_*`) sobre JPA; JPA apenas para consultas simples (máx. 3 joins) ou tabelas sem função.
2. **DTOs como `record` Java 21**; nomes e comentários em português; Javadoc de classe com o template do autor (ver code-rules).
3. **Frontend**: componentes `@tjma/angular-21` + grid Bootstrap seletivo (`row`/`col-md-*`, sem reboot); botões SOMENTE `TjButton` (`tj-button-primary` etc., com `TjButtonModule` importado) — nunca `btn btn-*`.
4. **Sentinela**: todo novo `@RestController` é um objeto de permissão — sempre alertar que precisa ser cadastrado/concedido no Sentinela.
5. **Convenção das funções do banco**: retorno via `pkg_sistema_util.fn_mensagem` (`GER-S001. ...` = sucesso); no backend validar com `ResultadoFuncao`.

## Documentação de apoio

- Planos e mapeamento do banco: vault Obsidian `C:\Users\anton\OneDrive\Documentos\TJMA\obsidian\scelus`
- Setup/carga do banco dev: `scelus-api/src/main/resources/db/seed-dev.sql` e `correcoes-schema.sql`
- Testes de API prontos: `scelus-api/api-tests.http`
