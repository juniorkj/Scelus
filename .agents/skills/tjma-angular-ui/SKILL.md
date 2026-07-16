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

## 7. Corrigindo bugs na própria biblioteca (`infra-angular-21`) e republicando

Quando o bug está no componente da lib (não no uso dele pelo app), o fix é feito no repositório irmão `C:\Users\anton\git\Scelus\infra-angular-21` (`projects/tjma/src/lib/...`), não em `scelus-web`. Casos já resolvidos assim nesta base: `TjStepper` (ordem de `ngAfterContentInit`) e `TjRadio` (não mostrava `*` de obrigatório nem mensagem de erro — faltava usar a infra de `_msgError`/`getMsgError()` já existente em `TjFormFieldControl`, que `TjInput`/`TjSelect` já usam).

### Passo a passo

1. Edite o componente em `infra-angular-21/projects/tjma/src/lib/...`.
2. Rode `npm run build` dentro de `infra-angular-21` — gera `dist/tjma/tjma-angular-21-0.0.1.tgz`.
3. **Publique no Nexus** — `scelus-web` (assim como `frottas-web` e `AFROJUS`) consome `@tjma/angular-21` **pelo registro**, não por `file:`:
   ```json
   "@tjma/angular-21": "0.0.1"
   ```
   ```bash
   cd infra-angular-21/dist/tjma
   npm publish --registry https://nexusrepo.tjma.jus.br/repository/npm-releases/
   ```
   O `.npmrc` do projeto (conta `tjma-aplicacao`, já versionado) normalmente não tem permissão de `INCLUSAO` em `npm-releases` — o `npm publish` falha com `ENEEDAUTH`/`403`, e é preciso pedir credenciais de publicação ao usuário (uma conta com permissão, ex. `analise`) a cada vez, só para este comando — nunca fixar essas credenciais em arquivo versionado.
   O consumo, porém, é sempre pelo registro **`npm-public`** (o `registry=` padrão do `.npmrc`, com as credenciais de `tjma-aplicacao` já configuradas) — publicar em `npm-releases` propaga automaticamente pra lá (Nexus trata `npm-public` como grupo que agrega `npm-releases`), então não é preciso nenhuma credencial especial para instalar depois de publicado.
4. Reinstale em `scelus-web`.

### ⚠️ Armadilha: reinstalar sem bump de versão (mesma versão `0.0.1`)

Como a versão nunca muda (decisão do projeto — não incrementar `0.0.1`), tanto o **cache do npm** quanto o **`package-lock.json`** guardam o hash de integridade (SHA-512) do tarball anterior. Um `npm install` normal depois de publicar de novo **silenciosamente instala a versão antiga** (sem erro, sem aviso) ou falha com `EINTEGRITY` — foi assim que o fix do `TjRadio` quase passou despercebido: build e publish deram certo, mas `node_modules/@tjma/angular-21` continuou com o conteúdo antigo até forçar a reinstalação.

**Sempre depois de publicar:**
```bash
cd scelus-web
rm -rf node_modules/@tjma/angular-21
npm install --no-audit --no-fund
```
Se mesmo assim o npm reclamar de integridade, use `--force` (`npm install --force`) — o hash antigo travado no lockfile é a causa mais comum.

**Depois de reinstalar, sempre confirme que o fix realmente entrou** antes de seguir (não confie só no "added 1 package"):
```bash
grep -c "<algo do seu fix>" node_modules/@tjma/angular-21/fesm2022/tjma-angular-21.mjs
```

> Durante uma correção específica desta sessão, o `package.json` chegou a apontar temporariamente para um tarball local (`file:../infra-angular-21/dist/tjma/tjma-angular-21-0.0.1.tgz`) só para validar o fix mais rápido sem depender do Nexus — mas isso **não é o padrão do projeto** e foi revertido para `"0.0.1"` (via registro), igual aos demais projetos TJMA. Não deixe esse atalho `file:` persistir no `package.json` depois de validar.

---

## 💡 Diretrizes e Padrões de Desenvolvimento (Mandatório)

1. **Preferência por Observables:** Sempre que aplicável, utilize e dê preferência ao uso de `Observable` (RxJS) para a gerência de dados e fluxos assíncronos no frontend Angular.
2. **Reutilização de Componentes Corporativos:** Utilize e reaproveite ao máximo os componentes e diretivas fornecidos pela biblioteca `@tjma/angular-21` para manter a consistência visual.
3. **Consulte a Documentação Interna:** Em caso de dúvidas sobre propriedades, inputs ou outputs de componentes corporativos, consulte as documentações e exemplos contidos no diretório [infra-angular-21](file:///C:/Users/anton/git/Scelus/infra-angular-21) do projeto.
4. **Layout com Bootstrap seletivo e botões TjButton:** siga a seção 6 acima — grid `row`/`col-md-*` para responsividade, sem `reboot`, sem `btn btn-*`, e `TjButtonModule` importado onde houver botão.

