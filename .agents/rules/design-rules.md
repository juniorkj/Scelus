---
trigger: glob
globs: "**/*.{tsx,jsx,vue,svelte,css,scss},**/components/**,**/app/**/page.tsx"
---

# Design Rules (TIER 2) - AG Kit

> Loaded when touching UI files. Design rules live in the specialist agents, NOT here.

## 🛑 GATE: DESIGN.md before any UI code (MANDATORY)

Before writing or editing UI (components, pages, styles — web or mobile), a **`DESIGN.md` must exist at the project root**.

1. **Check** for `DESIGN.md` at the project root.
2. **If missing:** infer the design direction from the brief, then **create `DESIGN.md` first** (tokens + rationale) following the `design-spec` skill. Do not write UI code until it exists.
3. **If present:** READ it and build strictly against its tokens. Descriptive names in prose map to token names.
4. **Keep it in sync** when the visual language changes — it is the single source of truth.

> Exception: none for new UI. A genuinely trivial tweak to existing UI (one button color, a spacing nudge) may proceed if a `DESIGN.md` already governs the project. Net-new UI always requires the gate.

| Need | Read |
| ---- | ---- |
| DESIGN.md format / tokens | `.agents/skills/design-spec/SKILL.md` |

---

| Task         | Read                            |
| ------------ | ------------------------------- |
| Web UI/UX    | `.agents/agent/frontend-specialist.md` |
| Mobile UI/UX | `.agents/agent/mobile-developer.md`    |

**These agents contain:**

- Purple Ban (no purple by default — brand/brief override allowed)
- Template Ban (no standard layouts)
- Anti-cliché rules
- Deep Design Thinking protocol

> 🔴 **For design work:** Open and READ the agent file. Rules are there.

## 🇧🇷 Regras de Frontend (TJMA)

1. **Observables:** Dar preferência para a utilização de `Observable` (RxJS) no frontend Angular sempre que aplicável.
2. **Reutilização de Componentes:** Reutilizar os componentes da biblioteca corporativa `@tjma/angular-21`.
3. **Consulte a Documentação Interna:** Consultar as documentações da infraestrutura em [infra-angular-21](file:///C:/Users/anton/git/Scelus/infra-angular-21) para garantir o uso correto dos componentes corporativos.

### Layout, responsividade e CSS (obrigatório)

4. **Grid Bootstrap para layout de formulários:** usar `row g-2` / `col-md-*` para dispor campos lado a lado com responsividade. O Bootstrap é importado **seletivamente** em `src/styles.scss` (functions/variables/mixins + `grid`, `tables`, `badge`, `alert`, `helpers`, `utilities`) — o **`reboot` e os botões do Bootstrap NÃO são importados** para não conflitar com o design system TJMA. Nunca usar classes `btn btn-*` do Bootstrap.
5. **Botões sempre via TjButton:** usar os atributos da infra em elementos nativos — `<button tj-button-primary>`, `tj-button-secondary`, `tj-button-danger`, com sufixos `-sm`/`-md`/`-lg` (ex.: `tj-button-danger-sm`). **Obrigatório importar `TjButtonModule`** no componente standalone, senão o seletor não é ativado e o botão fica sem estilo.
6. **Campos de formulário 100% da coluna:** os componentes `tj-input`/`tj-select`/`tj-date-picker`/`tj-textarea` são blocos de largura total — o tamanho do campo é controlado pela coluna do grid (`col-md-3`, `col-md-5`, etc.), nunca por width fixo no campo.
7. **Antes de usar uma classe utilitária, confirme que ela existe:** o projeto não importa o Bootstrap completo. Utilitários disponíveis: espaçamento (`mt-*`, `g-*`), texto (`fw-*`, `text-*`, `small`), flex (`align-items-*`, `d-flex`), `badge`/`bg-*`, `alert-*`, `table`/`table-responsive`. Se a tela parecer "sem estilo", a causa provável é classe sem CSS por trás ou módulo Tj não importado no componente.

---
