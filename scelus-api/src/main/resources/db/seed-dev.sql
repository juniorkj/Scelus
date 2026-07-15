-- ═══════════════════════════════════════════════════════════════════════════
-- Scelus — Setup e carga inicial do banco (dba_scelus)
-- Executado em DEV (posdleg) em 11/07/2026. Reexecutável (idempotente por contagem).
-- Para HOMOLOGAÇÃO/PRODUÇÃO: revisar com a equipe de banco antes de aplicar.
--
-- ⚠️ Defeitos encontrados no schema de DEV (reportar à equipe de banco):
--   1. pkg_sistema_util.fn_mensagem NÃO existia — todas as funções *_ins/_upd/_del
--      a referenciam e falhavam em runtime (criada abaixo).
--   2. As PKs de tb_polo, tb_estado_civil, tb_renda, tb_religiao, tb_raca_etnia
--      e tb_tipo_vinculo NÃO possuem identity/default — as funções fn_*_ins do
--      pkg_tabela_basica falham com not-null violation.
--   3. fn_escolaridade_ins e fn_situacao_uso_droga_ins tentam inserir valor em
--      coluna GENERATED ALWAYS (erro 428C9).
--   Por isso a carga abaixo usa INSERT direto (com id explícito onde necessário).
-- ═══════════════════════════════════════════════════════════════════════════

-- ── 1. pkg_sistema_util.fn_mensagem (faltante — usada por todas as funções) ──
CREATE OR REPLACE FUNCTION pkg_sistema_util.fn_mensagem(
    p_msg_id character varying,
    p_msg_comp character varying DEFAULT ''::character varying)
 RETURNS character varying
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_str_valor character varying(1000);
BEGIN
    BEGIN
        SELECT str_mensagem
          INTO STRICT v_str_valor
          FROM public.tb_mensagem
         WHERE upper(str_mensagem_id) = upper(p_msg_id);

        RETURN p_msg_id || '. ' || v_str_valor || ' ' || coalesce(p_msg_comp, '');

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE EXCEPTION USING
                ERRCODE = 'P0001',
                MESSAGE = format('Mensagem não cadastrada no Sistema: %s', p_msg_id);
        WHEN OTHERS THEN
            RAISE EXCEPTION USING
                ERRCODE = 'P0001',
                MESSAGE = format('Mensagem de Erro: %s (%s).', SQLERRM, SQLSTATE);
    END;
END;
$function$;

-- ── 2. Change Log (tabela do padrão frottas-new) ─────────────────────────────
CREATE TABLE IF NOT EXISTS public.tb_change_log (
    int_change_log_id  bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    str_release        character varying(200) NOT NULL,
    str_versao         character varying(20)  NOT NULL,
    str_descricao_item character varying(500) NOT NULL
);

INSERT INTO public.tb_change_log (str_release, str_versao, str_descricao_item)
SELECT r, v, d FROM (VALUES
    ('Versão 1.0.0 — Julho/2026', '1.0.0', 'CSU001 — Consulta dinâmica de crimes do processo com filtros simples e avançados.'),
    ('Versão 1.0.0 — Julho/2026', '1.0.0', 'CSU002 — Cadastro de crimes com importação de processo e partes do PJe.'),
    ('Versão 1.0.0 — Julho/2026', '1.0.0', 'CSU008 — Cadastro e vínculo de Medidas Protetivas de Urgência (MPU) com justificativa obrigatória.'),
    ('Versão 1.0.0 — Julho/2026', '1.0.0', 'Autenticação integrada ao Sentinela com controle de permissões por objeto.')
) AS t(r, v, d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_change_log);

-- ── 3. Domínios (id explícito nas tabelas sem identity) ──────────────────────
INSERT INTO public.tb_polo (int_polo_id, str_polo)
SELECT * FROM (VALUES (1, 'ATIVO'), (2, 'PASSIVO')) AS t(i, d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_polo);

INSERT INTO public.tb_estado_civil (int_estado_civil_id, str_estado_civil)
SELECT * FROM (VALUES
    (1, 'Solteiro(a)'), (2, 'Casado(a)'), (3, 'União Estável'),
    (4, 'Divorciado(a)'), (5, 'Viúvo(a)')) AS t(i, d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_estado_civil);

INSERT INTO public.tb_escolaridade (str_escolaridade)  -- identity OK
SELECT d FROM (VALUES
    ('Sem escolaridade'), ('Fundamental incompleto'), ('Fundamental completo'),
    ('Médio completo'), ('Superior completo'), ('Pós-graduação')) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_escolaridade);

INSERT INTO public.tb_renda (int_renda_id, str_renda)
SELECT * FROM (VALUES
    (1, 'Sem renda'), (2, 'Até 1 salário mínimo'), (3, 'De 1 a 3 salários mínimos'),
    (4, 'De 3 a 5 salários mínimos'), (5, 'Acima de 5 salários mínimos')) AS t(i, d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_renda);

INSERT INTO public.tb_religiao (int_religiao_id, str_religiao)
SELECT * FROM (VALUES
    (1, 'Católica'), (2, 'Evangélica'), (3, 'Espírita'),
    (4, 'Matriz Africana'), (5, 'Sem religião'), (6, 'Outra')) AS t(i, d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_religiao);

INSERT INTO public.tb_raca_etnia (int_raca_etnia_id, str_raca_etnia)
SELECT * FROM (VALUES
    (1, 'Branca'), (2, 'Preta'), (3, 'Parda'), (4, 'Amarela'), (5, 'Indígena')) AS t(i, d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_raca_etnia);

-- CSU002 (Telas 2.4/2.6): a combo de drogas deve oferecer "Não é usuário" e "Não especificado"
INSERT INTO public.tb_situacao_uso_droga (str_situacao_uso_droga)  -- identity OK
SELECT d FROM (VALUES
    ('Não usuário'), ('Usuário eventual'), ('Usuário frequente'), ('Em tratamento'),
    ('Não especificado')) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_situacao_uso_droga);

INSERT INTO public.tb_tipo_vinculo (int_tipo_vinculo_id, str_tipo_vinculo)
SELECT * FROM (VALUES
    (1, 'Cônjuge'), (2, 'Ex-cônjuge'), (3, 'Companheiro(a)'), (4, 'Ex-companheiro(a)'),
    (5, 'Namorado(a)'), (6, 'Ex-namorado(a)'), (7, 'Pai/Mãe'), (8, 'Filho(a)'),
    (9, 'Irmão(ã)'), (10, 'Outro')) AS t(i, d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_tipo_vinculo);

-- CSU008/RN008.05: a justificativa "Outros" é obrigatória (habilita observações complementares)
INSERT INTO public.tb_justificativa_inclusao_mpu (str_justificativa_inclusao_mpu)  -- identity OK
SELECT d FROM (VALUES
    ('Risco à integridade física da vítima'),
    ('Descumprimento de medida protetiva anterior'),
    ('Reiteração de ameaças pelo acusado'),
    ('Solicitação expressa da vítima'),
    ('Determinação judicial'),
    ('Outros')) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_justificativa_inclusao_mpu);

-- CSU002 (Tela 2.6): ocupação da vítima/acusado. Carga inicial curada a partir da
-- Classificação Brasileira de Ocupações (CBO/MTE) — não é a CBO completa (~2600
-- famílias), e sim uma amostra cobrindo os 10 Grandes Grupos, para dar cobertura
-- inicial ao combo. Cadastro de novas ocupações fica a cargo do SCELUS_CSU006
-- (o usuário pode incluir uma ocupação não listada durante o cadastro de crimes).
-- Código CBO mantido entre parênteses na descrição para rastreabilidade, pois
-- tb_ocupacao não possui coluna própria para o código.
INSERT INTO public.tb_ocupacao (str_ocupacao)  -- identity OK
SELECT d FROM (VALUES
    -- Grande Grupo 0 — Membros das forças armadas, policiais e bombeiros militares
    ('Militar (0000-00)'),
    ('Policial Militar (5172-05)'),
    ('Bombeiro Militar (5172-10)'),
    -- Grande Grupo 1 — Membros superiores do poder público, dirigentes e gerentes
    ('Gerente Administrativo (1415-10)'),
    ('Diretor de Empresa (1237-10)'),
    ('Servidor Público (Cargo de Direção) (1113-05)'),
    -- Grande Grupo 2 — Profissionais das ciências e das artes
    ('Advogado(a) (2410-05)'),
    ('Médico(a) (2251-**)'),
    ('Enfermeiro(a) (2235-05)'),
    ('Professor(a) (2394-**)'),
    ('Psicólogo(a) (2515-**)'),
    ('Assistente Social (2516-05)'),
    ('Contador(a) (2522-05)'),
    ('Engenheiro(a) (2142-**)'),
    -- Grande Grupo 3 — Técnicos de nível médio
    ('Técnico(a) em Enfermagem (3222-05)'),
    ('Técnico(a) em Segurança do Trabalho (3541-05)'),
    ('Técnico(a) Administrativo (4110-**)'),
    -- Grande Grupo 4 — Trabalhadores de serviços administrativos
    ('Auxiliar Administrativo (4110-05)'),
    ('Recepcionista (4221-05)'),
    ('Escriturário(a) (4110-10)'),
    -- Grande Grupo 5 — Trabalhadores dos serviços, vendedores do comércio
    ('Vendedor(a) (5211-05)'),
    ('Cabeleireiro(a) (5142-05)'),
    ('Cozinheiro(a) (5132-**)'),
    ('Garçom / Garçonete (5134-05)'),
    ('Cuidador(a) de Idosos (5162-10)'),
    ('Cuidador(a) de Crianças (5162-05)'),
    ('Trabalhador(a) Doméstico(a) (5165-05)'),
    ('Motorista (5192-**)'),
    ('Vigilante / Segurança (5173-**)'),
    ('Diarista (5165-10)'),
    -- Grande Grupo 6 — Trabalhadores agropecuários, florestais, da caça e pesca
    ('Agricultor(a) (6110-**)'),
    ('Pescador(a) (6220-05)'),
    ('Trabalhador(a) Rural (6220-10)'),
    -- Grande Grupo 7 — Trabalhadores da produção de bens e serviços industriais (I)
    ('Pedreiro(a) (7152-10)'),
    ('Eletricista (7156-**)'),
    ('Marceneiro(a) (7422-05)'),
    ('Mecânico(a) (9141-**)'),
    -- Grande Grupo 8 — Trabalhadores da produção de bens e serviços industriais (II)
    ('Operador(a) de Máquinas (8283-**)'),
    ('Costureiro(a) (7615-05)'),
    ('Padeiro(a) (8484-05)'),
    -- Grande Grupo 9 — Trabalhadores de manutenção e reparação
    ('Auxiliar de Serviços Gerais (5192-05)'),
    ('Pintor(a) (7233-**)'),
    ('Servente de Obras (7170-20)'),
    -- Situações sem vínculo formal de trabalho (frequentes em formulários sociais)
    ('Autônomo(a) (sem CBO específico)'),
    ('Do Lar (sem CBO específico)'),
    ('Desempregado(a) (sem CBO específico)'),
    ('Aposentado(a) / Pensionista (sem CBO específico)'),
    ('Estudante (sem CBO específico)'),
    ('Não informado')) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_ocupacao);

INSERT INTO public.tb_cep (str_cep, str_logradouro, str_bairro, str_municipio, str_uf)  -- identity OK
SELECT * FROM (VALUES
    ('65010-000', 'Rua Grande', 'Centro', 'São Luís', 'MA'),
    ('65015-430', 'Rua do Egito', 'Centro', 'São Luís', 'MA'),
    ('65045-260', 'Avenida dos Holandeses', 'Calhau', 'São Luís', 'MA'),
    ('65065-545', 'Avenida Daniel de la Touche', 'Cohama', 'São Luís', 'MA'),
    ('65110-000', 'Estrada de Ribamar', 'Centro', 'São José de Ribamar', 'MA')
) AS t(c, l, b, m, u)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_cep);

-- ── 4. Deficiências (LBI/IBGE) ────────────────────────────────────────────────
INSERT INTO public.tb_deficiencia (str_deficiencia)
SELECT d FROM (VALUES
    ('Física'),
    ('Auditiva'),
    ('Visual'),
    ('Intelectual'),
    ('Múltipla'),
    ('Transtorno do Espectro Autista (TEA)')
) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_deficiencia);

-- ── 5. Drogas (Formulários de risco/FONAR) ───────────────────────────────────
INSERT INTO public.tb_droga (str_droga)
SELECT d FROM (VALUES
    ('Álcool'),
    ('Maconha'),
    ('Cocaína'),
    ('Crack'),
    ('Inalantes/Solventes'),
    ('Anfetaminas/Estimulantes'),
    ('Alucinógenos'),
    ('Medicamentos/Sedativos sem receita'),
    ('Outra')
) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_droga);

-- ── 6. Benefícios Sociais / Programas de Apoio ───────────────────────────────
INSERT INTO public.tb_tipo_beneficio (str_tipo_beneficio)
SELECT d FROM (VALUES
    ('Bolsa Família'),
    ('Benefício de Prestação Continuada (BPC/LOAS)'),
    ('Auxílio-Gás'),
    ('Tarifa Social de Energia Elétrica/Água'),
    ('Minha Casa Minha Vida / Programa Habitacional'),
    ('Aposentadoria / Pensão por Morte (Previdência Social)'),
    ('Auxílio-Doença / Auxílio-Acidente'),
    ('Seguro-Desemprego'),
    ('Outro programa municipal/estadual')
) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_tipo_beneficio);

-- ── 7. Consequências da Violência (CNJ/FONAR) ────────────────────────────────
INSERT INTO public.tb_tipo_consequencia_violencia (str_tipo_consequencia_violencia)
SELECT d FROM (VALUES
    ('Lesões corporais / traumas físicos'),
    ('Sofrimento psíquico grave (ansiedade, depressão, pânico)'),
    ('Ideação ou tentativa de suicídio'),
    ('Afastamento ou perda de emprego / prejuízo financeiro'),
    ('Isolamento social / perda de contato com redes de apoio'),
    ('Danos a bens materiais ou pertences pessoais'),
    ('Abandono compulsório do lar / desalojamento'),
    ('Comprometimento da saúde de filhos / dependentes'),
    ('Agravamento de problemas de saúde preexistentes'),
    ('Gravidez ou infecções decorrentes de violência sexual')
) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_tipo_consequencia_violencia);

-- ── 8. Configuração Familiar (CSU002 Tela 2.4) ───────────────────────────────
INSERT INTO public.tb_tipo_configuracao_familiar (str_tipo_config_familiar)
SELECT d FROM (VALUES
    ('Nuclear'),
    ('Monoparental'),
    ('Extensa/Ampliada'),
    ('Reconstituída'),
    ('Unipessoal'),
    ('Homoafetiva'),
    ('Outra')
) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_tipo_configuracao_familiar);

-- ── 9. Tipo de Comunicante do Fato (CSU002 Tela 2.8) ─────────────────────────
INSERT INTO public.tb_tipo_comunicante (str_tipo_comunicante)
SELECT d FROM (VALUES
    ('Vítima'),
    ('Testemunha'),
    ('Familiar'),
    ('Vizinho(a)'),
    ('Policial'),
    ('Conselho Tutelar'),
    ('Escola'),
    ('Unidade de Saúde'),
    ('Anônimo'),
    ('Outro')
) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_tipo_comunicante);

-- ── 10. Escuta Judicial / Depoimento Especial (Lei 13.431/2017) ──────────────
INSERT INTO public.tb_escuta_judicial (str_escuta_judicial)
SELECT d FROM (VALUES
    ('Depoimento Especial'),
    ('Escuta Especializada'),
    ('Depoimento Sem Dano')
) AS t(d)
WHERE NOT EXISTS (SELECT 1 FROM public.tb_escuta_judicial);

-- ── 11. Posição na Prole (matriz prole × posição, CSU002 Tela 2.5) ───────────
-- Combinação numérica: para cada tamanho de prole (1 a 8 filhos), todas as
-- posições possíveis (1ª a última). Ex.: prole=3, posição=2 = "2º de 3 filhos".
INSERT INTO public.tb_posicao_prole (int_prole, int_posicao)
SELECT prole, posicao
  FROM generate_series(1, 8) AS prole,
       LATERAL generate_series(1, prole) AS posicao
WHERE NOT EXISTS (SELECT 1 FROM public.tb_posicao_prole);
