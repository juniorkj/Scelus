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
