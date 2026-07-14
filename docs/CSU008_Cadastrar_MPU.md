# CSU008 — Cadastrar Medidas Protetivas de Urgência (MPU)

## 1. Descrição
Permite associar Medidas Protetivas de Urgência (MPUs) a um Fato Ocorrido. O fluxo suporta:
1. O cadastro de **novas MPUs** diretamente na sessão.
2. A vinculação de **MPUs preexistentes** cadastradas para o mesmo par Acusado-Vítima no sistema (através do preenchimento de uma justificativa padronizada).

---

## 2. Atores e Pré-condições
* **Ator:** Servidor.
* **Pré-condições:**
  * Processo judicial selecionado.
  * Pelo menos um Fato Ocorrido cadastrado na sessão corrente.
  * Acionado a partir da tela de cadastro de Fatos Ocorridos (CSU002 - Tela 2.8).

---

## 3. Regras de Negócio (RN)
* **RN008.01 — Atributos da Nova MPU:** No momento de inserção manual, o sistema vincula automaticamente a MPU à vítima e ao acusado correspondentes ao par do fato ocorrido selecionado, registrando o processo de origem.
* **RN008.02 — Busca por Par Acusado-Vítima:** Ao selecionar um fato ocorrido na tela, o sistema busca todas as MPUs já existentes no banco para aquele mesmo par de partes (independentemente do processo original delas).
* **RN008.03 & RN008.04 — Vinculação Explícita:** Se houver MPUs anteriores, elas são exibidas para seleção individual pelo usuário, não ocorrendo vinculação automática.
* **RN008.05 — Justificativa Obrigatória:** Para cada MPU anterior vinculada, é exigida a seleção de uma justificativa cadastrada (`tb_justificativa_inclusao_mpu`). Caso seja do tipo "Outros", o campo de observações torna-se obrigatório.
* **RN008.07 — Escopo Temporário:** Toda associação de MPU inserida ou vinculada no CSU008 permanece em estado temporário na sessão de cadastro. A gravação definitiva no banco ocorre apenas na conclusão final do cadastro de crimes (CSU002).

---

## 4. Mapeamento de Tabelas Físicas

| Tabela Física | Campo Chave | Descrição |
| :--- | :--- | :--- |
| **`public.tb_medida_protetiva_urgencia`** | `int_mpu_id` | Cadastro da medida protetiva (Número da MPU, data da decisão, concessão, datas de intimação/ciência e desistência) |
| **`public.tb_fato_ocorrido_mpu`** | `int_fato_ocorrido_mpu_id` | Associa a MPU ao Fato Ocorrido (`tb_fato_ocorrido`) com a respectiva justificativa e observação |
| **`public.tb_justificativa_inclusao_mpu`** | `int_justificativa_inclusao_mpu_id` | Tabela de domínio com as justificativas padronizadas de vínculo |
| **`public.tb_fato_ocorrido`** | `int_fato_ocorrido_id` | Tabela de relacionamento do fato que ancora a MPU |
| **`public.tb_vitima`** | `int_vitima_id` | Vítima beneficiária da MPU |
| **`public.tb_acusado`** | `int_acusado_id` | Acusado alvo da MPU |
| **`public.tb_processo_crime`** | `int_processo_crime_id` | Processo judicial originário da MPU |
