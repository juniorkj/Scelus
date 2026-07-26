-- ═══════════════════════════════════════════════════════════════════════════
-- Scelus — CSU008: campo "Data Fim de Vigência" (dta_fim_vigencia) em MPU
-- Preparado em 26/07/2026 — revisão da spec CSU008 (.odt).
-- Aplicar em DEV (posdleg) e depois em HOMOLOGAÇÃO/PRODUÇÃO junto à equipe de banco.
--
-- Contexto: uma MPU é considerada "vigente" enquanto dta_fim_vigencia não for
-- preenchida. RN008.02 (busca automática por par vítima-acusado) passa a
-- considerar apenas MPUs vigentes.
-- ═══════════════════════════════════════════════════════════════════════════

ALTER TABLE public.tb_medida_protetiva_urgencia
    ADD COLUMN dta_fim_vigencia timestamp without time zone NULL;

-- As duas funções abaixo precisam ser recriadas com uma assinatura nova
-- (parâmetro adicional) — DROP explícito evita deixar as assinaturas antigas
-- (14/15 argumentos) como overloads mortos ao lado das novas (15/16 argumentos).

DROP FUNCTION IF EXISTS pkg_medida_protetiva.fn_medida_protetiva_urgencia_ins(
    character varying, timestamp without time zone, character varying,
    timestamp without time zone, timestamp without time zone, timestamp without time zone,
    timestamp without time zone, character varying, character varying, character varying,
    bigint, bigint, character varying, character varying);

CREATE OR REPLACE FUNCTION pkg_medida_protetiva.fn_medida_protetiva_urgencia_ins(
    p_str_legislacao_fundamento character varying,
    p_dta_decisao timestamp without time zone,
    p_bol_concedida character varying,
    p_dta_intimacao_acusado timestamp without time zone,
    p_dta_intimacao_vitima timestamp without time zone,
    p_dta_ciencia_vitima timestamp without time zone,
    p_dta_ciencia_acusado timestamp without time zone,
    p_bol_pedido_desistencia character varying,
    p_bol_inquerito_instaurado character varying,
    p_str_observacoes character varying,
    p_int_acusado_id bigint,
    p_int_vitima_id bigint,
    p_str_numero_mpu character varying,
    p_str_numero_unico character varying,
    p_dta_fim_vigencia timestamp without time zone DEFAULT NULL,
    OUT p_int_mpu_id bigint,
    OUT p_resultado character varying)
 RETURNS record
 LANGUAGE plpgsql
AS $function$
	/*
		Inclui medida_protetiva_urgencia
		Autor: Mauro França
		Solicitante: KingJr
		Data criação: 20260707
		Alteração: inclusão de dta_fim_vigencia (spec CSU008, 26/07/2026)
	*/

DECLARE
	v_msg		 character varying(500);
BEGIN

    BEGIN

		INSERT INTO public.tb_medida_protetiva_urgencia(
			   str_legislacao_fundamento
			 , dta_decisao
			 , bol_concedida
			 , dta_intimacao_acusado
			 , dta_intimacao_vitima
			 , dta_ciencia_vitima
			 , dta_ciencia_acusado
			 , bol_pedido_desistencia
			 , bol_inquerito_instaurado
			 , str_observacoes
			 , int_acusado_id
			 , int_vitima_id
			 , str_numero_mpu
			 , str_numero_unico
			 , dta_fim_vigencia
			 , dta_criacao
		)
	    VALUES (
			   p_str_legislacao_fundamento
			 , p_dta_decisao
			 , p_bol_concedida
			 , p_dta_intimacao_acusado
			 , p_dta_intimacao_vitima
			 , p_dta_ciencia_vitima
			 , p_dta_ciencia_acusado
			 , p_bol_pedido_desistencia
			 , p_bol_inquerito_instaurado
			 , p_str_observacoes
			 , p_int_acusado_id
			 , p_int_vitima_id
			 , p_str_numero_mpu
			 , p_str_numero_unico
			 , p_dta_fim_vigencia
			 , NOW()::timestamp
		  )
		RETURNING int_mpu_id
		INTO p_int_mpu_id;

		p_resultado := pkg_sistema_util.fn_mensagem('GER-S001');

    EXCEPTION

        WHEN SQLSTATE 'P0001' THEN  -- Caso dê ero na chamada das funções fn_usuario e fn_mensagem
		    GET STACKED DIAGNOSTICS v_msg = MESSAGE_TEXT;
			p_int_mpu_id:= NULL;
            p_resultado := pkg_sistema_util.fn_mensagem('GER-E001', v_msg);

        WHEN OTHERS THEN
            p_int_mpu_id:= NULL;
            p_resultado := pkg_sistema_util.fn_mensagem('GER-E001', format('pkg_medida_protetiva.fn_medida_protetiva_urgencia_ins - Mensagem de erro: %s (%s).', SQLERRM, SQLSTATE));

    END;
END;
$function$;

DROP FUNCTION IF EXISTS pkg_medida_protetiva.fn_medida_protetiva_urgencia_upd(
    bigint, character varying, timestamp without time zone, character varying,
    timestamp without time zone, timestamp without time zone, timestamp without time zone,
    timestamp without time zone, character varying, character varying, character varying,
    bigint, bigint, character varying, character varying);

CREATE OR REPLACE FUNCTION pkg_medida_protetiva.fn_medida_protetiva_urgencia_upd(
    p_int_mpu_id bigint,
    p_str_legislacao_fundamento character varying,
    p_dta_decisao timestamp without time zone,
    p_bol_concedida character varying,
    p_dta_intimacao_acusado timestamp without time zone,
    p_dta_intimacao_vitima timestamp without time zone,
    p_dta_ciencia_vitima timestamp without time zone,
    p_dta_ciencia_acusado timestamp without time zone,
    p_bol_pedido_desistencia character varying,
    p_bol_inquerito_instaurado character varying,
    p_str_observacoes character varying,
    p_int_acusado_id bigint,
    p_int_vitima_id bigint,
    p_str_numero_mpu character varying,
    p_str_numero_unico character varying,
    p_dta_fim_vigencia timestamp without time zone DEFAULT NULL,
    OUT p_resultado character varying)
 RETURNS character varying
 LANGUAGE plpgsql
AS $function$

	/*
		Atualiza medida_protetiva_urgencia
		Autor: Mauro França
		Solicitante: KingJr
		Data criação: 20260707
		Alteração: inclusão de dta_fim_vigencia (spec CSU008, 26/07/2026)
	*/

DECLARE
	v_msg		 character varying(500);
BEGIN

    BEGIN

			UPDATE public.tb_medida_protetiva_urgencia
				SET str_legislacao_fundamento = p_str_legislacao_fundamento
				  , dta_decisao = p_dta_decisao
				  , bol_concedida = p_bol_concedida
				  , dta_intimacao_acusado = p_dta_intimacao_acusado
				  , dta_intimacao_vitima = p_dta_intimacao_vitima
				  , dta_ciencia_vitima = p_dta_ciencia_vitima
				  , dta_ciencia_acusado = p_dta_ciencia_acusado
				  , bol_pedido_desistencia = p_bol_pedido_desistencia
				  , bol_inquerito_instaurado = p_bol_inquerito_instaurado
				  , str_observacoes = p_str_observacoes
				  , int_acusado_id = p_int_acusado_id
				  , int_vitima_id = p_int_vitima_id
				  , str_numero_mpu = p_str_numero_mpu
				  , str_numero_unico = p_str_numero_unico
				  , dta_fim_vigencia = p_dta_fim_vigencia
			WHERE int_mpu_id = p_int_mpu_id;

        IF NOT FOUND THEN
			v_msg:= 'Registro não encontrado para alteração.';
			RAISE EXCEPTION USING ERRCODE = 'P0099', MESSAGE = v_msg;
        END IF;

        p_resultado := pkg_sistema_util.fn_mensagem('GER-S002');

    EXCEPTION
	    WHEN SQLSTATE 'P0099' THEN
			p_resultado:= pkg_sistema_util.fn_mensagem('GER-E002', v_msg);

        WHEN SQLSTATE 'P0001' THEN  -- Caso dê ero na chamada das funções fn_usuario e fn_mensagem
		    GET STACKED DIAGNOSTICS v_msg = MESSAGE_TEXT;
            p_resultado := pkg_sistema_util.fn_mensagem('GER-E002', v_msg);

        WHEN OTHERS THEN
            p_resultado := pkg_sistema_util.fn_mensagem('GER-E002', format('pkg_medida_protetiva.fn_medida_protetiva_urgencia_upd - Mensagem de erro: %s (%s).', SQLERRM, SQLSTATE));

    END;

END;
$function$;
