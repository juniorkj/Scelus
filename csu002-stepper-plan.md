# Plano de Implementação — Formulário em Steps para CSU002 (Scelus)

Este plano descreve a reestruturação da tela de cadastro de crimes (**CSU002**) do projeto **Scelus**, dividindo o formulário em etapas utilizando o componente `TjStepper` da biblioteca de infraestrutura `@tjma/angular-21`.

---

## 1. Abordagem de Design e Componentes
O componente `tj-stepper` (herdado do Angular Material) será adotado na orientação horizontal para guiar o usuário em cada fase do fluxo transacional do CSU002. Toda a persistência continuará sendo realizada de forma única ao final do fluxo (no último passo, acionando o botão "Finalizar"), respeitando a regra **RN01**.

### Divisão dos Passos do Stepper:
1. **Passo 1: Processo (PJe)** — Busca do processo, visualização de detalhes em modo leitura (Processo, Polo Ativo/Passivo, Sentença).
2. **Passo 2: Benefícios & Configuração Familiar (Telas 2.3 e 2.4)** — Associação de partes a benefícios assistenciais (Data Início) e configurações familiares (Data de Declaração, Observações).
3. **Passo 3: Vítimas (Telas 2.5)** — Cadastro de múltiplas vítimas, perfil social, ocupações e seleção múltipla de drogas utilizadas.
4. **Passo 4: Acusados (Tela 2.6)** — Cadastro de múltiplos acusados, perfil social, antecedentes criminais, reincidência e drogas.
5. **Passo 5: Vínculos (Tela 2.7)** — Relacionamento (Vítima x Acusado x Tipo de Vínculo).
6. **Passo 6: Fatos Ocorridos & Finalização (Telas 2.8 e 2.9)** — Localização/CEP do Fato, individualização dos crimes, Comunicante do Fato e Consequências da Violência.

---

## 2. Tarefas e Componentes Envolvidos

### Frontend (`scelus-web`)

#### [MODIFY] [consultar-crimes-manter.component.html](file:///c:/Users/anton/git/Scelus/scelus-web/src/app/features/consultar-crimes/manter/consultar-crimes-manter.component.html)
* Importar e envolver o formulário no componente `<tj-stepper>`.
* Dividir os passos usando tags `<mat-step>`.
* Adicionar botões de navegação `<button tj-button-secondary matStepperPrevious>` e `<button tj-button-primary matStepperNext>`.
* **Benefícios e Configurações Familiares:** Criar tabelas/grids interativos para inclusão dinâmica com botão (+).
* **Vítimas e Acusados:**
  * Alterar a seleção simples de drogas para uma listagem dinâmica com botão (+) permitindo selecionar múltiplas substâncias (`tb_litigancia_droga`).
  * Implementar suporte para cadastrar múltiplas vítimas e acusados acumulando-os em um grid de resumo.
* **Fatos Ocorridos:** Adicionar campos para cadastrar o Comunicante e o Tipo de Comunicante.

#### [MODIFY] [consultar-crimes-manter.component.ts](file:///c:/Users/anton/git/Scelus/scelus-web/src/app/features/consultar-crimes/manter/consultar-crimes-manter.component.ts)
* Adicionar importação de `TjStepperModule` e `MatStepperModule`.
* Ajustar os arrays de dados temporários do componente para acumular múltiplos registros locais na memória antes de submeter ao backend (ex: `vitimasList`, `acusadosList`, `beneficiosList`, `vinculosList`, `fatosList`).
* Ajustar a montagem do payload final no método `cadastrar()`.

---

## 3. Plano de Verificação

### Testes Manuais (Simulação no Front):
* Rodar o projeto com `npm run start` e testar a navegação passo a passo do stepper.
* Validar se todos os campos obrigatórios da especificação acionam o validador visual antes de permitir avançar o step.
* Validar a adição e remoção dinâmica de itens nos grids locais (como múltiplas drogas, benefícios e fatos).
