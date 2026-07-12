---
name: tjma-angular-ui
description: Guia mestre da biblioteca @tjma/angular (Angular 21), consolidando o Storybook oficial, comandos de infraestrutura e padrões de reutilização.
---
# TJMA Angular UI — Guia Mestre de Infraestrutura e Componentes

Este guia é a referência definitiva para a utilização da biblioteca `@tjma/angular` v21+. Ele integra as diretrizes do **Storybook oficial** com os padrões arquiteturais do TJMA.

---

## 1. Instalação e Configuração

Para iniciar um novo projeto Angular 21 seguindo os padrões do TJMA:

```bash
# 1. Criar novo projeto (use scss)
ng new meu-projeto

# 2. Instalar biblioteca
npm i @tjma/angular

# 3. Configuração Inicial (Configura assets, estilos e dependências)
ng g @tjma/angular:ng-add
```

---

## 2. Geração de Features e Componentes (Schematics)

A biblioteca automatiza a criação de módulos CRUD e componentes de pesquisa.

| Comando | Descrição |
|---------|-----------|
| `ng g @tjma/angular:feature --name=nome` | Gera uma estrutura CRUD completa em `src/app/features` (Lista e Formulário). |
| `ng g @tjma/angular:selector --name=nome --path=caminho` | Gera um modal de pesquisa padronizado no caminho especificado. |

---

## 3. CDK: Lógica Core e Utilidades

### TjCrudBaseComponent e TjCrudService
Padrão obrigatório para telas de manutenção.
- **Service**: Deve estender `TjCrudService` e definir o `super('/path-da-api')`.
- **Component**: Estenda `TjCrudBaseComponent<Service>`.
- **Hooks (Public)**: `beforeCreate()`, `beforeUpdate()`, `afterCreate()`, `afterUpdate()`, `beforeDelete()`. Se retornarem `false`, interrompem o fluxo.

### Gestão de Estados (States)
Injete `new States()` para controle de feedback visual (loading, error, success, empty).
- `this.states.toSuccess(res)` / `this.states.toError(err)`.

---

## 4. Pages e Templates de Layout

### TjAuthTemplate & TjApp
- **`TjApp`**: Use no `app.component.html` para loading global e verificação de token via Sentinela.
- **`TjAuthTemplate`**: Wrapper para rotas autenticadas. Gerencia menu, perfil e cabeçalho.

---

## 5. System Components (Estrutura CRUD)

### TjTable
Tabela avançada integrada ao CRUD.
- **Inputs**: `[columns]`, `[client]`, `hidePlus`, `hideExportPDF`, `hideBatchDelete`.
- **Ações de Linha**: `<td tj-row-actions [showEdit]="true" [tjRowActionElement]="element">`.

---

## 6. Layout, Responsividade e CSS (Padrão do Projeto)

### Bootstrap seletivo (grid + utilitários, SEM reboot e SEM botões)

O `src/styles.scss` do scelus-web importa o Bootstrap **por módulos SCSS**, deliberadamente sem o `reboot` e sem os botões (que conflitariam com o design system TJMA):

```scss
@use '@tjma/angular-21/styles.scss' as tj;   // sempre primeiro

@import 'bootstrap/scss/functions';
@import 'bootstrap/scss/variables';
@import 'bootstrap/scss/variables-dark';
@import 'bootstrap/scss/maps';
@import 'bootstrap/scss/mixins';
@import 'bootstrap/scss/root';
@import 'bootstrap/scss/grid';        // row / col-md-* / g-*
@import 'bootstrap/scss/tables';
@import 'bootstrap/scss/badge';
@import 'bootstrap/scss/alert';
@import 'bootstrap/scss/helpers';
@import 'bootstrap/scss/utilities';
@import 'bootstrap/scss/utilities/api';
```

- **Formulários:** campos lado a lado com `<div class="row g-2"><div class="col-md-3">...` — os componentes `tj-input`/`tj-select`/`tj-date-picker`/`tj-textarea` ocupam 100% da coluna; o tamanho do campo é definido pela coluna do grid, nunca por width fixo.
- **Sintoma clássico:** se todos os campos ocupam a linha inteira, o grid não está sendo aplicado (classe sem CSS por trás). Confira o `styles.scss` antes de mexer nos templates.

### Botões — SOMENTE TjButton (nunca `btn btn-*` do Bootstrap)

```html
<button tj-button-primary (click)="salvar()">Salvar</button>
<button tj-button-secondary>Cancelar</button>
<button tj-button-danger-sm><tj-icon icon="Trash2"></tj-icon></button>
```

Variantes: `tj-button-{ghost|primary|secondary|danger|success|warning|information}` com sufixos opcionais `-sm`/`-md`/`-lg`. **Obrigatório adicionar `TjButtonModule` aos `imports` do componente standalone** — sem isso o seletor `button[tj-button-*]` não é ativado e o botão renderiza sem estilo.

---

## 💡 Diretrizes e Padrões de Desenvolvimento (Mandatório)

1. **Preferência por Observables:** Sempre que aplicável, utilize e dê preferência ao uso de `Observable` (RxJS) para a gerência de dados e fluxos assíncronos no frontend Angular.
2. **Reutilização de Componentes Corporativos:** Utilize e reaproveite ao máximo os componentes e diretivas fornecidos pela biblioteca `@tjma/angular-21` para manter a consistência visual.
3. **Consulte a Documentação Interna:** Em caso de dúvidas sobre propriedades, inputs ou outputs de componentes corporativos, consulte as documentações e exemplos contidos no diretório [infra-angular-21](file:///C:/Users/anton/git/Scelus/infra-angular-21) do projeto.
4. **Layout com Bootstrap seletivo e botões TjButton:** siga a seção 6 acima — grid `row`/`col-md-*` para responsividade, sem `reboot`, sem `btn btn-*`, e `TjButtonModule` importado onde houver botão.

