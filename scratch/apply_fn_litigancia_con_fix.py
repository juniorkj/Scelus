import psycopg2

conn = psycopg2.connect(
    host="posdleg.tjma.jus.br",
    port="5432",
    database="dba_scelus",
    user="dba_scelus",
    password="dba_scelus",
)

sql = r"""
CREATE OR REPLACE FUNCTION pkg_litigancia.fn_litigancia_con(p_int_litigancia_id bigint DEFAULT NULL::bigint, p_int_polo_id bigint DEFAULT NULL::bigint, p_str_numero_unico character varying DEFAULT NULL::character varying, p_int_parte_id bigint DEFAULT NULL::bigint, p_int_start_row integer DEFAULT NULL::integer, p_int_end_row integer DEFAULT NULL::integer)
 RETURNS TABLE(int_litigancia_id bigint, int_polo_id bigint, str_polo character varying, str_numero_unico character varying, int_parte_id bigint, str_parte character varying, int_situacao_uso_droga_id bigint, str_situacao_uso_droga character varying, int_estado_civil_id bigint, str_estado_civil character varying, int_escolaridade_id bigint, str_escolaridade character varying, int_renda_id bigint, str_renda character varying, int_religiao_id bigint, str_religiao character varying, int_posicao_prole_id bigint, int_prole bigint, int_posicao bigint, int_raca_etnia_id bigint, str_raca_etnia character varying, str_observacoes_posicao_prole character varying, int_reg bigint, int_total_count bigint)
 LANGUAGE plpgsql
AS $function$

	/*
		Consulta Litigancia
		Autor: Mauro França
		Solicitante: KingJr
		Data criação: 20260605
	*/

DECLARE
    v_tamanho_pagina integer;
    v_inicio_pagina  integer;
BEGIN
    -- Paginação...
    v_inicio_pagina :=
        CASE
            WHEN p_int_start_row = 0 THEN 1
            WHEN p_int_start_row IS NOT NULL THEN p_int_start_row
            ELSE 1
        END;

    v_tamanho_pagina :=
        CASE
            WHEN p_int_end_row IS NOT NULL THEN p_int_end_row
            ELSE 99999
        END;

    -- converte start/end row para OFFSET/LIMIT
    v_inicio_pagina  := v_inicio_pagina - 1;
    v_tamanho_pagina := v_tamanho_pagina - v_inicio_pagina;

    RETURN QUERY
    SELECT
			  rec_base.int_litigancia_id
			, rec_base.int_polo_id
			, rec_base.str_polo
			, rec_base.str_numero_unico
			, rec_base.int_parte_id
			, rec_base.str_parte
			, rec_base.int_situacao_uso_droga_id
			, rec_base.str_situacao_uso_droga
			, rec_base.int_estado_civil_id
			, rec_base.str_estado_civil
			, rec_base.int_escolaridade_id
			, rec_base.str_escolaridade
			, rec_base.int_renda_id
			, rec_base.str_renda
			, rec_base.int_religiao_id
			, rec_base.str_religiao
			, rec_base.int_posicao_prole_id
			, rec_base.int_prole
			, rec_base.int_posicao
			, rec_base.int_raca_etnia_id
			, rec_base.str_raca_etnia
			, rec_base.str_observacoes_posicao_prole,
        ROW_NUMBER() OVER (ORDER BY rec_base.int_litigancia_id DESC) AS int_reg,
        COUNT(*) OVER () AS  int_total_count
    FROM (
			SELECT l.int_litigancia_id
			     , l.int_polo_id
				 , p.str_polo
				 , l.str_numero_unico
				 , l.int_parte_id
			     , '???'::character varying as str_parte
				 , l.int_situacao_uso_droga_id
				 , sud.str_situacao_uso_droga
				 , l.int_estado_civil_id
				 , ec.str_estado_civil
				 , l.int_escolaridade_id
				 , e.str_escolaridade
				 , l.int_renda_id
				 , re.str_renda
				 , l.int_religiao_id
				 , rl.str_religiao
				 , l.int_posicao_prole_id
				 , pp.int_prole
				 , pp.int_posicao
				 , l.int_raca_etnia_id
				 , ret.str_raca_etnia
				 , l.str_observacoes_posicao_prole
			FROM public.tb_litigancia l
			left join tb_polo p on l.int_polo_id = p.int_polo_id
			--left join vw_parte
			left join public.tb_situacao_uso_droga sud on l.int_situacao_uso_droga_id = sud.int_situacao_uso_droga_id
			left join public.tb_estado_civil ec on l.int_estado_civil_id = ec.int_estado_civil_id
			left join public.tb_escolaridade e on l.int_escolaridade_id = e.int_escolaridade_id
			left join public.tb_renda re on l.int_renda_id = re.int_renda_id
			left join public.tb_religiao rl on l.int_religiao_id = rl.int_religiao_id
			left join public.tb_posicao_prole pp on l.int_posicao_prole_id = pp.int_posicao_prole_id
			left join public.tb_raca_etnia ret on l.int_raca_etnia_id = ret.int_raca_etnia_id

			where
            1=1
			  and (p_int_litigancia_id is null or l.int_litigancia_id = p_int_litigancia_id)
			  and (p_int_polo_id is null or l.int_polo_id = p_int_polo_id)
			  and (p_str_numero_unico is null or l.str_numero_unico = p_str_numero_unico)
			  and (p_int_parte_id is null or l.int_parte_id = p_int_parte_id)
    ) rec_base
    LIMIT v_tamanho_pagina
    OFFSET v_inicio_pagina;

END;
$function$;
"""

try:
    with conn.cursor() as cur:
        cur.execute(sql)
    conn.commit()
    print("OK: pkg_litigancia.fn_litigancia_con atualizada com sucesso em DEV.")

    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT pg_get_function_identity_arguments(p.oid) AS args
            FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace
            WHERE n.nspname = 'pkg_litigancia' AND p.proname = 'fn_litigancia_con'
            """
        )
        print("Assinatura atual:", cur.fetchone())
except Exception as e:
    conn.rollback()
    print("[ERRO] Falha ao aplicar a correção:", e)
finally:
    conn.close()
