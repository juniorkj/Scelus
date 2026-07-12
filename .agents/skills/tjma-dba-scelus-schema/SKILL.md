---
name: tjma-dba-scelus-schema
description: Schema completo do banco PostgreSQL dba_scelus (tabelas por schema e funções dos pacotes pkg_*) para consultas e desenvolvimento sem acesso ao banco.
---

# SKILL: tjma-dba-scelus-schema — Mapeamento do banco dba_scelus

> Fonte: levantamento no banco de DEV (posdleg.tjma.jus.br:5432/dba_scelus). Para inspeção em tempo real use o MCP `postgres-scelus` (registrado em `.mcp.json`).

## Convenções das funções (obrigatório saber)

- Todas as funções `fn_*_ins/upd/del` retornam mensagem via `pkg_sistema_util.fn_mensagem`: formato `"GER-S001. Inclusão realizada com sucesso."` — prefixo `XXX-S` = sucesso, `XXX-E` = erro. No backend, validar com o record `ResultadoFuncao` (comum).
- Funções `*_ins` retornam record com `p_int_*_id` (id gerado) + `p_resultado`; `*_upd`/`*_del` retornam apenas o varchar `p_resultado`.
- Chamada via SQL: `SELECT p_int_x_id AS id, p_resultado AS mensagem FROM pkg_x.fn_x_ins(:params)` (ins) ou `SELECT pkg_x.fn_x_del(:id)` (del/upd).
- ⚠️ Defeitos conhecidos corrigidos em DEV via `scelus-api/src/main/resources/db/correcoes-schema.sql` (fn_mensagem ausente, PKs sem identity, fn_*_ins do pkg_tabela_basica). `fn_prole_ins` segue quebrada (referencia tb_prole inexistente).
- Tabelas SEM função de escrita (usar JPA): tb_processo_crime, tb_vitima, tb_acusado, tb_vinculo, tb_cep, tb_tipo_vinculo, tb_justificativa_inclusao_mpu, tb_change_log.
# Mapeamento de Tabelas e Funções — PostgreSQL

Este documento contém o levantamento completo das tabelas e funções do banco de dados `dba_scelus` para subsidiar a criação das entidades JPA no backend Java.

## 🗂️ Tabelas por Schema

### Schema `dba_comum`
- **tb_municipio**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_municipio_id` | `bigint` | `NO` |
  | `str_sigla_uf_id` | `character varying` | `NO` |
  | `str_municipio` | `character varying` | `NO` |
  </details>

- **tb_uf**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_pais_id` | `integer` | `NO` |
  | `str_sigla_uf_id` | `character varying` | `NO` |
  | `str_nome_uf` | `character varying` | `NO` |
  | `str_codigo_ibge` | `character varying` | `YES` |
  </details>

- **vm_dia_util**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `dta_dia_util` | `date` | `NO` |
  </details>

- **vm_funcionario**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_matricula_id` | `bigint` | `NO` |
  | `str_funcionario` | `character varying` | `NO` |
  | `chr_sexo` | `character varying` | `YES` |
  | `dta_nascimento` | `timestamp without time zone` | `YES` |
  | `str_cpf` | `character varying` | `YES` |
  | `int_unidade_trabalho_id` | `bigint` | `YES` |
  | `int_cargo_id` | `bigint` | `YES` |
  | `dta_ingresso` | `timestamp without time zone` | `YES` |
  | `dta_historico_situacao` | `timestamp without time zone` | `YES` |
  | `bol_ativo` | `character varying` | `YES` |
  | `int_funcao_id` | `bigint` | `YES` |
  | `str_simbolo` | `character varying` | `YES` |
  | `str_cargo` | `character varying` | `YES` |
  | `int_comarca_id` | `bigint` | `YES` |
  | `str_comarca` | `character varying` | `YES` |
  | `str_endereco` | `character varying` | `YES` |
  | `str_cep` | `character varying` | `YES` |
  | `str_email` | `character varying` | `YES` |
  | `str_telefone` | `character varying` | `YES` |
  | `int_situacao_funcional_id` | `bigint` | `YES` |
  | `str_situacao_funcional` | `character varying` | `YES` |
  | `dat_exercicio` | `timestamp without time zone` | `YES` |
  | `bol_juiz` | `character varying` | `YES` |
  | `bol_desembargador` | `character varying` | `YES` |
  | `str_nivel_servidor` | `character varying` | `YES` |
  | `int_codigo_banco_id` | `bigint` | `YES` |
  | `str_banco` | `character varying` | `YES` |
  | `str_agencia` | `character varying` | `YES` |
  | `str_conta_banco` | `character varying` | `YES` |
  | `str_conta_banco_dv` | `character varying` | `YES` |
  | `mon_vencimento` | `double precision` | `YES` |
  | `mon_vale_transporte` | `double precision` | `YES` |
  | `int_condicao_servidorid` | `bigint` | `YES` |
  | `str_condicao_servidor` | `character varying` | `YES` |
  | `dat_historico_situacao` | `timestamp without time zone` | `YES` |
  | `str_funcionario_social` | `character varying` | `YES` |
  | `str_funcionario_original` | `character varying` | `YES` |
  | `int_tipo_documento_id` | `character varying` | `YES` |
  | `str_numero_documento` | `character varying` | `YES` |
  | `dta_documento` | `timestamp without time zone` | `YES` |
  </details>

- **vm_unidade_trabalho**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_unidade_trabalho_id` | `bigint` | `YES` |
  | `str_unidade_trabalho` | `character varying` | `YES` |
  | `int_orgao_id` | `bigint` | `YES` |
  | `int_comarca_id` | `bigint` | `YES` |
  | `bol_inativo` | `character varying` | `YES` |
  | `int_tipo_area_id` | `bigint` | `YES` |
  | `str_tipo_area` | `character varying` | `YES` |
  | `int_tipo_unidade_trabalho_id` | `bigint` | `YES` |
  | `str_tipo_unidade_trabalho` | `character varying` | `YES` |
  | `int_centro_custo_id` | `bigint` | `YES` |
  | `str_centro_custo` | `character varying` | `YES` |
  | `str_email_unidade` | `character varying` | `YES` |
  | `str_telefone_unidade` | `character varying` | `YES` |
  </details>

### Schema `dba_sentinela`
- **tb_componente_grupo**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `str_sistema_id` | `character varying` | `NO` |
  | `int_grupo_usuario_id` | `bigint` | `NO` |
  | `str_usuario_id` | `character varying` | `NO` |
  | `dta_cadastro_grupo` | `timestamp without time zone` | `NO` |
  | `ind_usuario_ativo_grupo` | `character varying` | `NO` |
  | `dta_expiracao_grupo` | `timestamp without time zone` | `YES` |
  | `str_associador_id` | `character varying` | `NO` |
  | `dta_inicio_grupo` | `timestamp without time zone` | `NO` |
  | `str_documento_oficial` | `character varying` | `YES` |
  | `str_upd_usuario_id` | `character varying` | `YES` |
  | `str_upd_hostname` | `character varying` | `YES` |
  | `dta_upd_operacao` | `timestamp without time zone` | `YES` |
  </details>

- **tb_grupo_usuario**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `str_sistema_id` | `character varying` | `NO` |
  | `int_grupo_usuario_id` | `bigint` | `NO` |
  | `str_grupo_usuario` | `character varying` | `NO` |
  | `str_descricao_grupo_usuario` | `character varying` | `YES` |
  | `ind_administrador` | `character varying` | `NO` |
  | `str_identificador_grupo` | `character varying` | `NO` |
  | `str_upd_usuario_id` | `character varying` | `YES` |
  | `str_upd_hostname` | `character varying` | `YES` |
  | `dta_upd_operacao` | `timestamp without time zone` | `YES` |
  | `ind_default` | `character varying` | `YES` |
  </details>

- **tb_mensagem**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `str_mensagem_id` | `character varying` | `NO` |
  | `str_mensagem` | `character varying` | `NO` |
  | `dta_criacao` | `timestamp without time zone` | `YES` |
  | `str_procedimento` | `character varying` | `YES` |
  </details>

- **tb_objeto_sistema**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `str_sistema_id` | `character varying` | `NO` |
  | `str_objeto_id` | `character varying` | `NO` |
  | `str_objeto` | `character varying` | `NO` |
  | `str_descricao_objeto` | `character varying` | `YES` |
  | `str_objeto_pai_id` | `character varying` | `YES` |
  | `str_upd_usuario_id` | `character varying` | `YES` |
  | `str_upd_hostname` | `character varying` | `YES` |
  | `dta_upd_operacao` | `timestamp without time zone` | `YES` |
  </details>

- **tb_permissao_acesso**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `str_sistema_id` | `character varying` | `NO` |
  | `int_grupo_usuario_id` | `bigint` | `NO` |
  | `str_objeto_id` | `character varying` | `NO` |
  | `ind_atualizacao` | `character varying` | `NO` |
  | `ind_inclusao` | `character varying` | `NO` |
  | `ind_exclusao` | `character varying` | `NO` |
  | `ind_leitura` | `character varying` | `NO` |
  | `str_upd_usuario_id` | `character varying` | `YES` |
  | `str_upd_hostname` | `character varying` | `YES` |
  | `dta_upd_operacao` | `timestamp without time zone` | `YES` |
  </details>

- **tb_sistema**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `str_sistema_id` | `character varying` | `NO` |
  | `str_sistema` | `character varying` | `NO` |
  | `str_identificador_sistema` | `character varying` | `NO` |
  | `ind_solicita_documento` | `character varying` | `NO` |
  | `str_url` | `character varying` | `YES` |
  | `str_upd_usuario_id` | `character varying` | `YES` |
  | `str_upd_hostname` | `character varying` | `YES` |
  | `dta_upd_operacao` | `timestamp without time zone` | `YES` |
  | `bol_ativo` | `character varying` | `NO` |
  | `str_url_permissoes` | `character varying` | `YES` |
  | `bol_privado` | `character varying` | `YES` |
  | `str_descricao_sistema` | `character varying` | `YES` |
  </details>

- **tb_usuario**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `str_usuario_id` | `character varying` | `NO` |
  | `int_matricula` | `bigint` | `YES` |
  | `str_usuario` | `character varying` | `YES` |
  | `str_login` | `character varying` | `YES` |
  | `str_senha` | `character varying` | `YES` |
  | `ind_status_usuario` | `character varying` | `YES` |
  | `dta_cadastro_usuario` | `timestamp without time zone` | `YES` |
  | `dta_validade_usuario` | `timestamp without time zone` | `YES` |
  | `str_email_usuario` | `character varying` | `YES` |
  | `ind_servidor` | `character varying` | `YES` |
  | `dta_expira_senha` | `timestamp without time zone` | `YES` |
  | `dta_ult_login` | `timestamp without time zone` | `YES` |
  | `str_token` | `character varying` | `YES` |
  | `int_unidade_trabalho` | `bigint` | `YES` |
  | `dta_unidade_trabalho` | `timestamp without time zone` | `YES` |
  | `str_secret_key` | `character varying` | `YES` |
  | `str_usuario_registro` | `character varying` | `YES` |
  | `ind_2af_obrigatorio` | `character varying` | `YES` |
  </details>

- **tb_usuario_sid**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `str_usuario_id` | `character varying` | `YES` |
  | `int_session_id` | `integer` | `NO` |
  | `dta_atribuicao` | `timestamp without time zone` | `YES` |
  | `str_hostname` | `character varying` | `YES` |
  </details>

### Schema `public`
- **tb_acusado**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_acusado_id` | `bigint` | `NO` |
  | `int_litigancia_id` | `bigint` | `NO` |
  | `bol_possui_antecedentes` | `bigint` | `YES` |
  | `bol_reincidente` | `bigint` | `YES` |
  | `str_observacao_antecedentes` | `character varying` | `YES` |
  </details>

- **tb_beneficio**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_beneficio_id` | `bigint` | `NO` |
  | `int_tipo_beneficio_id` | `bigint` | `NO` |
  | `dta_data_inicio` | `timestamp without time zone` | `YES` |
  | `int_litigancia_id` | `bigint` | `NO` |
  </details>

- **tb_cep**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_cep_id` | `bigint` | `NO` |
  | `str_cep` | `character varying` | `YES` |
  | `str_logradouro` | `character varying` | `YES` |
  | `str_bairro` | `character varying` | `YES` |
  | `str_municipio` | `character varying` | `YES` |
  | `str_uf` | `character varying` | `YES` |
  | `int_cod_ibge_municipio` | `character varying` | `YES` |
  | `dec_latitude` | `bigint` | `YES` |
  | `dec_longitude` | `bigint` | `YES` |
  </details>

- **tb_comunicante**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_comunicante_id` | `bigint` | `NO` |
  | `str_nome` | `character varying` | `NO` |
  | `str_telefone` | `character varying` | `YES` |
  | `str_email` | `character varying` | `YES` |
  | `str_cpf_cnpj` | `character varying` | `YES` |
  </details>

- **tb_configuracao_familiar**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_configuracao_familiar_id` | `bigint` | `NO` |
  | `dta_declaracao` | `timestamp without time zone` | `YES` |
  | `str_observacao` | `character varying` | `YES` |
  | `int_tipo_configuracao_familiar_id` | `bigint` | `NO` |
  | `int_vitima_id` | `bigint` | `NO` |
  </details>

- **tb_consequencia_violencia**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_consequencia_violencia_id` | `bigint` | `NO` |
  | `int_tipo_consequencia_violencia_id` | `bigint` | `NO` |
  | `int_litigancia_id` | `bigint` | `NO` |
  | `str_observacao` | `character varying` | `YES` |
  </details>

- **tb_deficiencia**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_deficiencia_id` | `bigint` | `NO` |
  | `str_deficiencia` | `character varying` | `NO` |
  </details>

- **tb_droga**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_droga_id` | `bigint` | `NO` |
  | `str_droga` | `character varying` | `NO` |
  </details>

- **tb_escolaridade**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_escolaridade_id` | `bigint` | `NO` |
  | `str_escolaridade` | `character varying` | `NO` |
  | `bol_publico` | `bigint` | `YES` |
  | `int_ano_serie` | `bigint` | `YES` |
  </details>

- **tb_escuta_judicial**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_escuta_judicial_id` | `bigint` | `NO` |
  | `str_escuta_judicial` | `character varying` | `YES` |
  </details>

- **tb_estado_civil**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_estado_civil_id` | `bigint` | `NO` |
  | `str_estado_civil` | `character varying` | `NO` |
  </details>

- **tb_fato_ocorrido**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_acusado_id` | `bigint` | `NO` |
  | `int_vitima_id` | `bigint` | `NO` |
  | `int_fato_ocorrido_id` | `bigint` | `NO` |
  | `dta_data_fato` | `timestamp without time zone` | `NO` |
  | `int_cep_id` | `bigint` | `NO` |
  | `bol_medida_protetiva` | `character varying` | `NO` |
  | `int_processo_crime_id` | `bigint` | `YES` |
  | `dta_criacao` | `timestamp without time zone` | `NO` |
  </details>

- **tb_fato_ocorrido_comunicante**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_fato_ocorrido_comunicante_id` | `bigint` | `NO` |
  | `dta_denuncia` | `timestamp without time zone` | `YES` |
  | `str_observacao` | `character varying` | `YES` |
  | `bol_anonimizado` | `bigint` | `YES` |
  | `int_comunicante_id` | `bigint` | `NO` |
  | `int_tipo_comunicante_id` | `bigint` | `NO` |
  | `int_fato_ocorrido_id` | `bigint` | `NO` |
  </details>

- **tb_fato_ocorrido_mpu**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_fato_ocorrido_mpu_id` | `bigint` | `NO` |
  | `int_mpu_id` | `bigint` | `NO` |
  | `int_fato_ocorrido_id` | `bigint` | `NO` |
  | `int_justificativa_inclusao_mpu_id` | `bigint` | `NO` |
  | `dta_criacao` | `timestamp without time zone` | `NO` |
  | `str_observacao_justificativa` | `character varying` | `YES` |
  </details>

- **tb_justificativa_inclusao_mpu**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_justificativa_inclusao_mpu_id` | `bigint` | `NO` |
  | `str_justificativa_inclusao_mpu` | `character varying` | `YES` |
  </details>

- **tb_litigancia**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_litigancia_id` | `bigint` | `NO` |
  | `int_polo_id` | `bigint` | `NO` |
  | `str_numero_unico` | `character varying` | `NO` |
  | `int_parte_id` | `bigint` | `NO` |
  | `int_situacao_uso_droga_id` | `bigint` | `YES` |
  | `int_estado_civil_id` | `bigint` | `YES` |
  | `int_escolaridade_id` | `bigint` | `YES` |
  | `int_renda_id` | `bigint` | `YES` |
  | `int_religiao_id` | `bigint` | `YES` |
  | `int_posicao_prole_id` | `bigint` | `YES` |
  | `str_observacoes_posicao_prole` | `character varying` | `YES` |
  | `int_raca_etnia_id` | `bigint` | `YES` |
  | `dta_criacao` | `timestamp without time zone` | `NO` |
  | `dta_alteracao` | `timestamp without time zone` | `YES` |
  </details>

- **tb_litigancia_deficiencia**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_litigancia_deficiencia_id` | `bigint` | `NO` |
  | `int_deficiencia_id` | `bigint` | `NO` |
  | `int_litigancia_id` | `bigint` | `NO` |
  </details>

- **tb_litigancia_droga**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_litigancia_droga_id` | `bigint` | `NO` |
  | `int_droga_id` | `bigint` | `NO` |
  | `int_litigancia_id` | `bigint` | `NO` |
  </details>

- **tb_litigancia_escuta_judicial**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_litigancia_escuta_judicial_id` | `bigint` | `NO` |
  | `int_escuta_judicial_id` | `bigint` | `NO` |
  | `int_litigancia_id` | `bigint` | `NO` |
  </details>

- **tb_litigancia_ocupacao**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_litigancia_ocupacao_id` | `bigint` | `NO` |
  | `int_litigancia_id` | `bigint` | `NO` |
  | `int_ocupacao_id` | `bigint` | `NO` |
  </details>

- **tb_medida_protetiva_urgencia**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_mpu_id` | `bigint` | `NO` |
  | `str_legislacao_fundamento` | `character varying` | `NO` |
  | `dta_decisao` | `timestamp without time zone` | `NO` |
  | `bol_concedida` | `character varying` | `NO` |
  | `dta_intimacao_acusado` | `timestamp without time zone` | `NO` |
  | `dta_intimacao_vitima` | `timestamp without time zone` | `NO` |
  | `dta_ciencia_vitima` | `timestamp without time zone` | `YES` |
  | `dta_ciencia_acusado` | `timestamp without time zone` | `YES` |
  | `bol_pedido_desistencia` | `character varying` | `NO` |
  | `bol_inquerito_instaurado` | `character varying` | `NO` |
  | `str_observacoes` | `character varying` | `YES` |
  | `int_acusado_id` | `bigint` | `YES` |
  | `int_vitima_id` | `bigint` | `YES` |
  | `str_numero_mpu` | `character varying` | `YES` |
  | `str_numero_unico` | `character varying` | `YES` |
  | `dta_criacao` | `timestamp without time zone` | `NO` |
  </details>

- **tb_mensagem**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `str_mensagem_id` | `character varying` | `NO` |
  | `str_mensagem` | `character varying` | `NO` |
  | `dta_criacao` | `timestamp without time zone` | `YES` |
  | `str_procedimento` | `character varying` | `YES` |
  </details>

- **tb_ocupacao**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_ocupacao_id` | `bigint` | `NO` |
  | `str_ocupacao` | `character varying` | `NO` |
  </details>

- **tb_polo**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_polo_id` | `bigint` | `NO` |
  | `str_polo` | `character varying` | `YES` |
  </details>

- **tb_posicao_prole**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_posicao_prole_id` | `bigint` | `NO` |
  | `int_prole` | `bigint` | `YES` |
  | `int_posicao` | `bigint` | `YES` |
  </details>

- **tb_processo_crime**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_processo_crime_id` | `bigint` | `NO` |
  | `dta_inicio_tipificacao` | `timestamp without time zone` | `NO` |
  | `dta_fim_tipificacao` | `timestamp without time zone` | `YES` |
  | `int_codigo_assunto` | `bigint` | `NO` |
  | `str_numero_unico` | `character varying` | `NO` |
  </details>

- **tb_raca_etnia**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_raca_etnia_id` | `bigint` | `NO` |
  | `str_raca_etnia` | `character varying` | `NO` |
  </details>

- **tb_religiao**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_religiao_id` | `bigint` | `NO` |
  | `str_religiao` | `character varying` | `NO` |
  </details>

- **tb_renda**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_renda_id` | `bigint` | `NO` |
  | `str_renda` | `character varying` | `NO` |
  </details>

- **tb_situacao_uso_droga**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_situacao_uso_droga_id` | `bigint` | `NO` |
  | `str_situacao_uso_droga` | `character varying` | `NO` |
  </details>

- **tb_tipo_beneficio**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_tipo_beneficio_id` | `bigint` | `NO` |
  | `str_tipo_beneficio` | `character varying` | `YES` |
  </details>

- **tb_tipo_comunicante**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_tipo_comunicante_id` | `bigint` | `NO` |
  | `str_tipo_comunicante` | `character varying` | `NO` |
  </details>

- **tb_tipo_configuracao_familiar**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_tipo_configuracao_familiar_id` | `bigint` | `NO` |
  | `str_tipo_config_familiar` | `character varying` | `YES` |
  </details>

- **tb_tipo_consequencia_violencia**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_tipo_consequencia_violencia_id` | `bigint` | `NO` |
  | `str_tipo_consequencia_violencia` | `character varying` | `YES` |
  </details>

- **tb_tipo_vinculo**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_tipo_vinculo_id` | `bigint` | `NO` |
  | `str_tipo_vinculo` | `character varying` | `NO` |
  </details>

- **tb_vinculo**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_vinculo_id` | `bigint` | `NO` |
  | `int_tipo_vinculo_id` | `bigint` | `NO` |
  | `int_vitima_id` | `bigint` | `NO` |
  | `int_acusado_id` | `bigint` | `NO` |
  | `str_observacao` | `character varying` | `YES` |
  </details>

- **tb_vitima**
  <details><summary>Ver colunas</summary>

  | Coluna | Tipo | Nullable |
  | :--- | :--- | :--- |
  | `int_vitima_id` | `bigint` | `NO` |
  | `int_litigancia_id` | `bigint` | `NO` |
  | `int_cep_id` | `bigint` | `NO` |
  </details>

## ⚙️ Funções e Procedures por Schema (Módulos/Packages)

### Schema `pkg_fato_ocorrido` (Pacote `PKG_FATO_OCORRIDO`)
| Nome da Função | Retorno | Argumentos |
| :--- | :--- | :--- |
| `fn_fato_ocorrido_del` | `character varying` | `p_int_fato_ocorrido_id bigint, OUT p_resultado character varying` |
| `fn_fato_ocorrido_ins` | `record` | `p_int_acusado_id bigint, p_int_vitima_id bigint, p_dta_data_fato timestamp without time zone, p_int_cep_id bigint, p_bol_medida_protetiva character varying, p_int_processo_crime_id bigint, OUT p_int_fato_ocorrido_id bigint, OUT p_resultado character varying` |
| `fn_fato_ocorrido_mpu_del` | `character varying` | `p_int_fato_ocorrido_mpu_id bigint, OUT p_resultado character varying` |
| `fn_fato_ocorrido_mpu_ins` | `record` | `p_int_mpu_id bigint, p_int_fato_ocorrido_id bigint, p_int_justificativa_inclusao_mpu_id bigint, p_str_observacao_justificativa character varying, OUT p_int_fato_ocorrido_mpu_id bigint, OUT p_resultado character varying` |
| `fn_fato_ocorrido_mpu_upd` | `character varying` | `p_int_fato_ocorrido_mpu_id bigint, p_int_mpu_id bigint, p_int_fato_ocorrido_id bigint, p_int_justificativa_inclusao_mpu_id bigint, p_str_observacao_justificativa character varying, OUT p_resultado character varying` |
| `fn_fato_ocorrido_upd` | `character varying` | `p_int_fato_ocorrido_id bigint, p_int_acusado_id bigint, p_int_vitima_id bigint, p_dta_data_fato timestamp without time zone, p_int_cep_id bigint, p_bol_medida_protetiva character varying, p_int_processo_crime_id bigint, OUT p_resultado character varying` |

### Schema `pkg_litigancia` (Pacote `PKG_LITIGANCIA`)
| Nome da Função | Retorno | Argumentos |
| :--- | :--- | :--- |
| `fn_litigancia_beneficio_ins` | `record` | `p_int_litigancia_id bigint, p_int_tipo_beneficio_id bigint, p_dta_data_inicio timestamp without time zone, OUT p_int_beneficio_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_con` | `record` | `p_int_litigancia_id bigint DEFAULT NULL::bigint, p_int_polo_id bigint DEFAULT NULL::bigint, p_str_numero_unico bigint DEFAULT NULL::bigint, p_int_parte_id bigint DEFAULT NULL::bigint, p_int_start_row integer DEFAULT NULL::integer, p_int_end_row integer DEFAULT NULL::integer` |
| `fn_litigancia_deficiencia_del` | `character varying` | `p_int_litigancia_deficiencia_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_deficiencia_ins` | `record` | `p_int_deficiencia_id bigint, p_int_litigancia_id bigint, OUT p_int_litigancia_deficiencia_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_del` | `character varying` | `p_int_litigancia_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_droga_del` | `character varying` | `p_int_litigancia_droga_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_droga_ins` | `record` | `p_int_droga_id bigint, p_int_litigancia_id bigint, OUT p_int_litigancia_droga_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_escuta_judicial_del` | `character varying` | `p_int_litigancia_escuta_judicial_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_escuta_judicial_ins` | `record` | `p_int_escuta_judicial_id bigint, p_int_litigancia_id bigint, OUT p_int_litigancia_escuta_judicial_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_ins` | `record` | `p_int_polo_id bigint, p_str_numero_unico character varying, p_int_parte_id bigint, p_int_situacao_uso_droga_id bigint, p_int_estado_civil_id bigint, p_int_escolaridade_id bigint, p_int_renda_id bigint, p_int_religiao_id bigint, p_int_posicao_prole_id bigint, p_int_raca_etnia_id bigint, p_str_observacoes_posicao_prole character varying, OUT p_int_litigancia_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_ocupacao_del` | `character varying` | `p_int_litigancia_ocupacao_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_ocupacao_ins` | `record` | `p_int_ocupacao_id bigint, p_int_litigancia_id bigint, OUT p_int_litigancia_ocupacao_id bigint, OUT p_resultado character varying` |
| `fn_litigancia_upd` | `character varying` | `p_int_litigancia_id bigint, p_int_polo_id bigint, p_str_numero_unico character varying, p_int_parte_id bigint, p_int_situacao_uso_droga_id bigint, p_int_estado_civil_id bigint, p_int_escolaridade_id bigint, p_int_renda_id bigint, p_int_religiao_id bigint, p_int_posicao_prole_id bigint, p_int_raca_etnia_id bigint, p_str_observacoes_posicao_prole character varying, OUT p_resultado character varying` |

### Schema `pkg_medida_protetiva` (Pacote `PKG_MEDIDA_PROTETIVA`)
| Nome da Função | Retorno | Argumentos |
| :--- | :--- | :--- |
| `fn_medida_protetiva_urgencia_del` | `character varying` | `p_int_mpu_id bigint, OUT p_resultado character varying` |
| `fn_medida_protetiva_urgencia_ins` | `record` | `p_str_legislacao_fundamento character varying, p_dta_decisao timestamp without time zone, p_bol_concedida character varying, p_dta_intimacao_acusado timestamp without time zone, p_dta_intimacao_vitima timestamp without time zone, p_dta_ciencia_vitima timestamp without time zone, p_dta_ciencia_acusado timestamp without time zone, p_bol_pedido_desistencia character varying, p_bol_inquerito_instaurado character varying, p_str_observacoes character varying, p_int_acusado_id bigint, p_int_vitima_id bigint, p_str_numero_mpu character varying, p_str_numero_unico character varying, OUT p_int_mpu_id bigint, OUT p_resultado character varying` |
| `fn_medida_protetiva_urgencia_upd` | `character varying` | `p_int_mpu_id bigint, p_str_legislacao_fundamento character varying, p_dta_decisao timestamp without time zone, p_bol_concedida character varying, p_dta_intimacao_acusado timestamp without time zone, p_dta_intimacao_vitima timestamp without time zone, p_dta_ciencia_vitima timestamp without time zone, p_dta_ciencia_acusado timestamp without time zone, p_bol_pedido_desistencia character varying, p_bol_inquerito_instaurado character varying, p_str_observacoes character varying, p_int_acusado_id bigint, p_int_vitima_id bigint, p_str_numero_mpu character varying, p_str_numero_unico character varying, OUT p_resultado character varying` |

### Schema `pkg_processo` (Pacote `PKG_PROCESSO`)
| Nome da Função | Retorno | Argumentos |
| :--- | :--- | :--- |
| `fn_processo_parte_pje_con` | `record` | `p_int_parte_id bigint DEFAULT NULL::bigint, p_int_processo_id integer DEFAULT NULL::integer` |
| `fn_processo_pje_con` | `record` | `p_str_numero_unico_formatado character varying DEFAULT NULL::character varying` |

### Schema `pkg_sistema_util` (Pacote `PKG_SISTEMA_UTIL`)
| Nome da Função | Retorno | Argumentos |
| :--- | :--- | :--- |
| `fn_sessao` | `character varying` | `p_str_usuario_id character varying, p_str_host character varying, OUT p_resultado character varying` |
| `fn_usuario` | `character varying` | `*Nenhum*` |

### Schema `pkg_tabela_basica` (Pacote `PKG_TABELA_BASICA`)
| Nome da Função | Retorno | Argumentos |
| :--- | :--- | :--- |
| `fn_escolaridade_del` | `character varying` | `p_int_escolaridade_id bigint, OUT p_resultado character varying` |
| `fn_escolaridade_ins` | `record` | `p_str_escolaridade character varying, OUT p_int_escolaridade_id bigint, OUT p_resultado character varying` |
| `fn_estado_civil_del` | `character varying` | `p_int_estado_civil_id bigint, OUT p_resultado character varying` |
| `fn_estado_civil_ins` | `record` | `p_str_estado_civil character varying, OUT p_int_estado_civil_id bigint, OUT p_resultado character varying` |
| `fn_polo_del` | `character varying` | `p_int_polo_id bigint, OUT p_resultado character varying` |
| `fn_polo_ins` | `record` | `p_str_polo character varying, OUT p_int_polo_id bigint, OUT p_resultado character varying` |
| `fn_posicao_prole_del` | `character varying` | `p_int_posicao_prole_id bigint, OUT p_resultado character varying` |
| `fn_prole_ins` | `record` | `p_str_prole character varying, OUT p_int_prole_id bigint, OUT p_resultado character varying` |
| `fn_raca_etnia_del` | `character varying` | `p_int_raca_etnia_id bigint, OUT p_resultado character varying` |
| `fn_raca_etnia_ins` | `record` | `p_str_raca_etnia character varying, OUT p_int_raca_etnia_id bigint, OUT p_resultado character varying` |
| `fn_religiao_del` | `character varying` | `p_int_religiao_id bigint, OUT p_resultado character varying` |
| `fn_religiao_ins` | `record` | `p_str_religiao character varying, OUT p_int_religiao_id bigint, OUT p_resultado character varying` |
| `fn_renda_del` | `character varying` | `p_int_renda_id bigint, OUT p_resultado character varying` |
| `fn_renda_ins` | `record` | `p_str_renda character varying, OUT p_int_renda_id bigint, OUT p_resultado character varying` |
| `fn_situacao_uso_droga_del` | `character varying` | `p_int_situacao_uso_droga_id bigint, OUT p_resultado character varying` |
| `fn_situacao_uso_droga_ins` | `record` | `p_str_situacao_uso_droga character varying, OUT p_int_situacao_uso_droga_id bigint, OUT p_resultado character varying` |

### Schema `public` (Pacote `PUBLIC`)
| Nome da Função | Retorno | Argumentos |
| :--- | :--- | :--- |
| `dblink` | `record` | `text, boolean` |
| `dblink` | `record` | `text` |
| `dblink` | `record` | `text, text, boolean` |
| `dblink` | `record` | `text, text` |
| `dblink` | `record` | `text, boolean` |
| `dblink` | `record` | `text` |
| `dblink` | `record` | `text, text, boolean` |
| `dblink` | `record` | `text, text` |
| `dblink` | `record` | `text, boolean` |
| `dblink` | `record` | `text` |
| `dblink` | `record` | `text, text` |
| `dblink` | `record` | `text, boolean` |
| `dblink` | `record` | `text` |
| `dblink` | `record` | `text, text, boolean` |
| `dblink` | `record` | `text, text` |
| `dblink` | `record` | `text, text, boolean` |
| `dblink_build_sql_delete` | `text` | `text, int2vector, integer, text[]` |
| `dblink_build_sql_insert` | `text` | `text, int2vector, integer, text[], text[]` |
| `dblink_build_sql_update` | `text` | `text, int2vector, integer, text[], text[]` |
| `dblink_cancel_query` | `text` | `text` |
| `dblink_close` | `text` | `text, text, boolean` |
| `dblink_close` | `text` | `text, text` |
| `dblink_close` | `text` | `text, boolean` |
| `dblink_close` | `text` | `text` |
| `dblink_close` | `text` | `text, text, boolean` |
| `dblink_close` | `text` | `text, text` |
| `dblink_close` | `text` | `text, boolean` |
| `dblink_close` | `text` | `text` |
| `dblink_close` | `text` | `text, text, boolean` |
| `dblink_close` | `text` | `text, text` |
| `dblink_close` | `text` | `text, boolean` |
| `dblink_close` | `text` | `text` |
| `dblink_close` | `text` | `text, text, boolean` |
| `dblink_close` | `text` | `text, text` |
| `dblink_close` | `text` | `text, boolean` |
| `dblink_close` | `text` | `text` |
| `dblink_connect` | `text` | `text, text` |
| `dblink_connect` | `text` | `text` |
| `dblink_connect` | `text` | `text, text` |
| `dblink_connect` | `text` | `text` |
| `dblink_connect_u` | `text` | `text` |
| `dblink_connect_u` | `text` | `text, text` |
| `dblink_connect_u` | `text` | `text` |
| `dblink_connect_u` | `text` | `text, text` |
| `dblink_current_query` | `text` | `*Nenhum*` |
| `dblink_disconnect` | `text` | `text` |
| `dblink_disconnect` | `text` | `*Nenhum*` |
| `dblink_disconnect` | `text` | `text` |
| `dblink_disconnect` | `text` | `*Nenhum*` |
| `dblink_error_message` | `text` | `text` |
| `dblink_exec` | `text` | `text, boolean` |
| `dblink_exec` | `text` | `text, text` |
| `dblink_exec` | `text` | `text` |
| `dblink_exec` | `text` | `text, boolean` |
| `dblink_exec` | `text` | `text, text` |
| `dblink_exec` | `text` | `text, text, boolean` |
| `dblink_exec` | `text` | `text` |
| `dblink_exec` | `text` | `text, boolean` |
| `dblink_exec` | `text` | `text, text` |
| `dblink_exec` | `text` | `text, text, boolean` |
| `dblink_exec` | `text` | `text` |
| `dblink_exec` | `text` | `text, boolean` |
| `dblink_exec` | `text` | `text, text` |
| `dblink_exec` | `text` | `text, text, boolean` |
| `dblink_exec` | `text` | `text` |
| `dblink_exec` | `text` | `text, text, boolean` |
| `dblink_fdw_validator` | `void` | `options text[], catalog oid` |
| `dblink_fetch` | `record` | `text, integer` |
| `dblink_fetch` | `record` | `text, integer, boolean` |
| `dblink_fetch` | `record` | `text, text, integer` |
| `dblink_fetch` | `record` | `text, text, integer, boolean` |
| `dblink_fetch` | `record` | `text, integer, boolean` |
| `dblink_fetch` | `record` | `text, integer` |
| `dblink_fetch` | `record` | `text, integer` |
| `dblink_fetch` | `record` | `text, integer, boolean` |
| `dblink_fetch` | `record` | `text, text, integer` |
| `dblink_fetch` | `record` | `text, text, integer, boolean` |
| `dblink_fetch` | `record` | `text, integer` |
| `dblink_fetch` | `record` | `text, integer, boolean` |
| `dblink_fetch` | `record` | `text, text, integer` |
| `dblink_fetch` | `record` | `text, text, integer, boolean` |
| `dblink_fetch` | `record` | `text, text, integer` |
| `dblink_fetch` | `record` | `text, text, integer, boolean` |
| `dblink_get_connections` | `ARRAY` | `*Nenhum*` |
| `dblink_get_notify` | `record` | `OUT notify_name text, OUT be_pid integer, OUT extra text` |
| `dblink_get_notify` | `record` | `OUT notify_name text, OUT be_pid integer, OUT extra text` |
| `dblink_get_notify` | `record` | `conname text, OUT notify_name text, OUT be_pid integer, OUT extra text` |
| `dblink_get_notify` | `record` | `conname text, OUT notify_name text, OUT be_pid integer, OUT extra text` |
| `dblink_get_pkey` | `USER-DEFINED` | `text` |
| `dblink_get_result` | `record` | `text, boolean` |
| `dblink_get_result` | `record` | `text` |
| `dblink_get_result` | `record` | `text, boolean` |
| `dblink_get_result` | `record` | `text` |
| `dblink_is_busy` | `integer` | `text` |
| `dblink_open` | `text` | `text, text, boolean` |
| `dblink_open` | `text` | `text, text, text, boolean` |
| `dblink_open` | `text` | `text, text, text` |
| `dblink_open` | `text` | `text, text, boolean` |
| `dblink_open` | `text` | `text, text` |
| `dblink_open` | `text` | `text, text, text, boolean` |
| `dblink_open` | `text` | `text, text, text` |
| `dblink_open` | `text` | `text, text, boolean` |
| `dblink_open` | `text` | `text, text` |
| `dblink_open` | `text` | `text, text, text, boolean` |
| `dblink_open` | `text` | `text, text, text` |
| `dblink_open` | `text` | `text, text, boolean` |
| `dblink_open` | `text` | `text, text` |
| `dblink_open` | `text` | `text, text, text, boolean` |
| `dblink_open` | `text` | `text, text, text` |
| `dblink_open` | `text` | `text, text` |
| `dblink_send_query` | `integer` | `text, text` |
| `fn_send_email` | `text` | `p_to text, p_subject text, p_body text, p_from text DEFAULT 'no-reply@tjma.jus.br'::text, p_mimetype text DEFAULT 'html'::text` |
| `postgres_fdw_disconnect` | `boolean` | `text` |
| `postgres_fdw_disconnect_all` | `boolean` | `*Nenhum*` |
| `postgres_fdw_get_connections` | `record` | `OUT server_name text, OUT valid boolean` |
| `postgres_fdw_handler` | `fdw_handler` | `*Nenhum*` |
| `postgres_fdw_validator` | `void` | `text[], oid` |
| `trg_fix_foreign_permissions` | `event_trigger` | `*Nenhum*` |
| `user_search` | `record` | `uname text` |




