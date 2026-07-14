# CSU002 — Cadastrar Crimes do Processo

## 1. Descrição
Permite o cadastro dos fatos ocorridos, crimes e o perfil demográfico dos envolvidos (Vítima e Acusado) a partir de um processo judicial importado do PJe do TJMA.

---

## 2. Atores e Pré-condições
* **Ator:** Servidor.
* **Pré-condições:**
  * Base do PJe acessível via dblink.
  * Tela de cadastro aberta a partir do comando "Novo" no CSU001.

---

## 3. Fluxo de Cadastro e Telas
1. **Selecionar Processo (Tela 2.1):** Consulta o processo pelo número único CNJ. Retorna os dados cadastrais básicos e as partes envolvidas do PJe.
2. **Cadastrar Crimes Cometidos (Tela 2.2):** Exibe o assunto principal e crimes importados.
3. **Benefícios (Tela 2.3):** Associa partes a benefícios assistenciais (com data de início).
4. **Configuração Familiar (Tela 2.4):** Vincula partes às suas respectivas composições de convívio familiar.
5. **Cadastrar Vítima (Tela 2.5) & Acusado (Tela 2.6):** 
   * Preenchimento do perfil social: Gênero, Raça, Estado Civil, Religião, Escolaridade, Ocupação, CEP de residência, Uso de substâncias/drogas.
   * Registro de antecedentes e reincidência exclusivos para o Acusado.
6. **Vínculos (Tela 2.7):** Vincula a vítima ao acusado definindo o tipo de relação familiar/afetiva.
7. **Fatos Ocorridos (Tela 2.8):** Individualização da agressão, relacionando Crime x Vítima x Acusado com CEP do ocorrido e dados do Comunicante.
8. **Consequências da Violência (Tela 2.9):** Consequências corporais ou psicológicas decorrentes do fato.

---

## 4. Regras de Negócio (RN)
* **RN01 — Persistência Integral:** Toda a gravação dos dados em banco é transacional e deve ser consolidada apenas ao final do fluxo (ao acionar "Finalizar").
* **RN02 — Regras de Registro de Drogas/Substâncias:**
  * Se o valor selecionado indicar que o envolvido **não é usuário** ou a informação **não foi especificada**, o sistema **não** insere registros em `tb_litigancia_droga`.
  * Se houver drogas associadas à parte, o campo `id_situacao_uso_droga` da litigância deve obrigatoriamente estar preenchido com um status compatível com uso.

---

## 5. Mapeamento de Tabelas Físicas

| Tabela Física | Campo Chave | Descrição |
| :--- | :--- | :--- |
| **`public.tb_processo_crime`** | `int_processo_crime_id` | Armazena o número único do processo e assunto |
| **`public.tb_litigancia`** | `int_litigancia_id` | Histórico socioeconômico/demográfico da parte no processo |
| **`public.tb_vitima`** | `int_vitima_id` | Vincula a litigância à condição de vítima com seu CEP residencial |
| **`public.tb_acusado`** | `int_acusado_id` | Vincula a litigância ao acusado (antecedentes e reincidência) |
| **`public.tb_vinculo`** | `int_vinculo_id` | Tabela de relacionamento entre Vítima e Acusado |
| **`public.tb_fato_ocorrido`** | `int_fato_ocorrido_id` | Relaciona o Processo, Crime, Vítima, Acusado e CEP do fato |
| **`public.tb_comunicante`** | `int_comunicante_id` | Armazena dados cadastrais de quem noticiou o fato |
| **`public.tb_fato_ocorrido_comunicante`** | `int_fato_ocorrido_comunicante_id` | Associa o comunicante e seu tipo ao Fato Ocorrido |
| **`public.tb_consequencia_violencia`** | `int_consequencia_violencia_id` | Armazena as consequências físicas/psicológicas da agressão |
| **`public.tb_litigancia_raca_etnia`** | N/A | Tabela de associação N:M de raça/etnia |
| **`public.tb_litigancia_ocupacao`** | N/A | Tabela de associação N:M de ocupações |
| **`public.tb_litigancia_droga`** | N/A | Tabela de associação N:M de uso de substâncias |
| **`public.tb_litigancia_deficiencia`** | N/A | Tabela de associação N:M de deficiências |
| **`public.tb_litigancia_escuta_judicial`** | N/A | Vincula a parte ao registro de escuta/depoimento especial |
| **`public.tb_beneficio`** | `int_beneficio_id` | Registra benefícios percebidos pela parte no processo |
| **`public.tb_configuracao_familiar`** | `int_configuracao_familiar_id` | Composição familiar declarada pela vítima |
| **`public.tb_cep`** | `int_cep_id` | Tabela geral de endereços por CEP |
