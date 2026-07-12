import psycopg2

sql_query = """
SELECT f.int_fato_ocorrido_id AS idFatoOcorrido,
       p.str_numero_unico AS numeroProcesso,
       p.int_codigo_assunto AS codigoAssunto,
       f.dta_data_fato AS dataFato,
       f.bol_medida_protetiva AS medidaProtetiva,
       v_pje.str_nome AS nomeVitima,
       v_pje.str_cpf_cnpj AS cpfVitima,
       a_pje.str_nome AS nomeAcusado,
       a_pje.str_cpf_cnpj AS cpfAcusado,
       tv.str_tipo_vinculo AS tipoVinculo,
       (SELECT string_agg(d.str_deficiencia, ', ')
          FROM public.tb_litigancia_deficiencia ld
          JOIN public.tb_deficiencia d ON d.int_deficiencia_id = ld.int_deficiencia_id
         WHERE ld.int_litigancia_id = l_vt.int_litigancia_id) AS deficienciaVitima
FROM public.tb_fato_ocorrido f
LEFT JOIN public.tb_processo_crime p ON f.int_processo_crime_id = p.int_processo_crime_id
LEFT JOIN public.tb_cep c ON f.int_cep_id = c.int_cep_id
LEFT JOIN public.tb_vitima vt ON f.int_vitima_id = vt.int_vitima_id
LEFT JOIN public.tb_litigancia l_vt ON vt.int_litigancia_id = l_vt.int_litigancia_id
LEFT JOIN public.tb_acusado ac ON f.int_acusado_id = ac.int_acusado_id
LEFT JOIN public.tb_litigancia l_ac ON ac.int_litigancia_id = l_ac.int_litigancia_id
LEFT JOIN public.tb_vinculo vi ON vi.int_vitima_id = vt.int_vitima_id AND vi.int_acusado_id = ac.int_acusado_id
LEFT JOIN public.tb_tipo_vinculo tv ON vi.int_tipo_vinculo_id = tv.int_tipo_vinculo_id
LEFT JOIN LATERAL pkg_processo.fn_processo_parte_pje_con(l_vt.int_parte_id, p.int_processo_crime_id::integer) v_pje ON true
LEFT JOIN LATERAL pkg_processo.fn_processo_parte_pje_con(l_ac.int_parte_id, p.int_processo_crime_id::integer) a_pje ON true
WHERE 1=1
LIMIT 5 OFFSET 0;
"""

try:
    conn = psycopg2.connect(
        host="posdleg.tjma.jus.br",
        port="5432",
        database="dba_scelus",
        user="dba_scelus",
        password="dba_scelus"
    )
    with conn.cursor() as cursor:
        cursor.execute(sql_query)
        print("Query executada com sucesso! Resultados:")
        for row in cursor.fetchall():
            print(row)
except Exception as e:
    print("Erro ao executar query:", e)
