import psycopg2

conn = psycopg2.connect(
    host="posdleg.tjma.jus.br",
    port="5432",
    database="dba_scelus",
    user="dba_scelus",
    password="dba_scelus",
)

try:
    with conn.cursor() as cur:
        cur.execute(
            """
            UPDATE public.tb_fato_ocorrido
               SET bol_medida_protetiva = 'N'
             WHERE int_fato_ocorrido_id = 19
            """
        )
        print(f"Linhas afetadas: {cur.rowcount}")
    conn.commit()

    with conn.cursor() as cur:
        cur.execute(
            "SELECT int_fato_ocorrido_id, bol_medida_protetiva FROM public.tb_fato_ocorrido WHERE int_fato_ocorrido_id = 19"
        )
        print("Valor atual:", cur.fetchone())
except Exception as e:
    conn.rollback()
    print("[ERRO]", e)
finally:
    conn.close()
