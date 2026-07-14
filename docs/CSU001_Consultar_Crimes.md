# CSU001 — Consultar Crimes do Processo

## 1. Descrição
Permite ao usuário consultar os crimes cadastrados no sistema Scelus a partir de filtros simples, filtros avançados e filtros compostos (com base em atributos do processo, fato ocorrido, vítima, acusado, vínculos, consequências e Medidas Protetivas de Urgência).
A partir da consulta é possível visualizar, alterar ou iniciar o fluxo de novo cadastro.

---

## 2. Atores e Pré-condições
* **Ator:** Servidor.
* **Pré-condições:**
  * Usuário autenticado no sistema.
  * Permissão de consulta concedida.
  * Existência de dados previamente cadastrados.

---

## 3. Regras de Negócio (RN)
* **RN001.01 — Filtros Cumulativos (AND):** Filtros simples e avançados diferentes são aplicados cumulativamente usando a operação `AND`.
* **RN001.02 — Filtros Não Cumulativos (OR):** Em filtros compostos de mesma categoria (ex: múltiplas drogas selecionadas), a pesquisa deve aplicar a condição `OR`.

---

## 4. Mapeamento de Tabelas e Atributos

### Tela 1.1 — Filtros Simples (Grid Principal)
| Campo | Atributo Físico / Fonte | Descrição |
| :--- | :--- | :--- |
| **Processo** | `public.tb_processo_crime.str_numero_unico` | Número CNJ do processo judicial |
| **Crime** | `public.tb_processo_crime.int_codigo_assunto` | Assunto/classe do crime cometida |
| **Vítima** | `public.tb_litigancia.int_parte_id` -> PJe | Nome/documento da Vítima |
| **Acusado** | `public.tb_litigancia.int_parte_id` -> PJe | Nome/documento do Acusado |
| **Tipo de Vínculo** | `public.tb_vinculo.int_tipo_vinculo_id` | Tipo de relacionamento familiar/pessoal |
| **Consequência da Violência** | `public.tb_consequencia_violencia.int_tipo_consequencia_violencia_id` | Tipificação do impacto da agressão |
| **MPU** | Verificação em `public.tb_fato_ocorrido_mpu` | **'S'** se houver MPU associada ao fato; **'N'** caso contrário. |
| **Deficiência da Vítima** | `public.tb_litigancia_deficiencia` | Registros de deficiências da vítima |

### Tela 1.2 — Filtros Avançados
* Filtros demográficos aplicados de forma condicional à Vítima ou ao Acusado:
  * **Nome / CPF** (`vw_parte.str_nome` / `vw_parte.str_cpf_cnpj`)
  * **Ocupação** (`tb_ocupacao.str_ocupacao` via `tb_litigancia_ocupacao`)
  * **Deficiência** (`tb_deficiencia.str_deficiencia` via `tb_litigancia_deficiencia`)
  * **Estado Civil** (`tb_estado_civil.str_estado_civil` via `tb_litigancia`)
  * **Religião** (`tb_religiao.str_religiao` via `tb_litigancia`)
  * **Escolaridade** (`tb_escolaridade.str_escolaridade` via `tb_litigancia`)
  * **Faixa de Renda** (`tb_renda.str_renda` via `tb_litigancia`)
* Filtros relacionados às Medidas Protetivas (`tb_medida_protetiva_urgencia`):
  * **Data da Decisão** (`dta_decisao`)
  * **Legislação** (`str_legislacao_fundamento`)
  * **Foi Concedida?** (`bol_concedida`)
  * **Data de Intimação (Acusado/Vítima)** (`dta_intimacao_acusado` / `dta_intimacao_vitima`)
  * **Data de Ciência (Acusado/Vítima)** (`dta_ciencia_acusado` / `dta_ciencia_vitima`)
  * **Há pedido de desistência?** (`bol_pedido_desistencia`)

### Tela 1.3 — Filtros Compostos (Seleção Múltipla)
* Permite pesquisar por múltiplos valores em:
  * **Drogas Utilizadas** (`tb_litigancia_droga` -> `tb_droga.str_droga`)
  * **Ocupações** (`tb_litigancia_ocupacao` -> `tb_ocupacao.str_ocupacao`)
  * **Deficiências** (`tb_litigancia_deficiencia` -> `tb_deficiencia.str_deficiencia`)
  * **Raça / Etnia** (`tb_litigancia_raca_etnia` -> `tb_raca_etnia.str_raca_etnia`)
  * **Benefícios Sociais** (`tb_beneficio` -> `tb_tipo_beneficio.str_tipo_beneficio`)
  * **Consequências da Violência** (`tb_consequencia_violencia` -> `tb_tipo_consequencia_violencia.str_tipo_consequencia_violencia`)
