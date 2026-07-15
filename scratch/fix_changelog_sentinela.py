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
            UPDATE public.tb_change_log
               SET str_descricao_item = %s
             WHERE int_change_log_id = 4
            """,
            ("Autenticação integrada ao Sentinela com controle de permissões por objeto.",),
        )
        print(f"Linhas afetadas: {cur.rowcount}")
    conn.commit()

    with conn.cursor() as cur:
        cur.execute(
            "SELECT str_descricao_item FROM public.tb_change_log WHERE int_change_log_id = 4"
        )
        print("Valor atual:", cur.fetchone())
except Exception as e:
    conn.rollback()
    print("[ERRO]", e)
finally:
    conn.close()
