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
            DROP FUNCTION pkg_litigancia.fn_litigancia_con(
                bigint, bigint, bigint, bigint, integer, integer
            );
            """
        )
    conn.commit()
    print("OK: overload antigo (p_str_numero_unico bigint) removido.")

    with conn.cursor() as cur:
        cur.execute(
            """
            SELECT p.oid, pg_get_function_identity_arguments(p.oid) AS args
            FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace
            WHERE n.nspname = 'pkg_litigancia' AND p.proname = 'fn_litigancia_con'
            """
        )
        print("Overloads restantes:", cur.fetchall())
except Exception as e:
    conn.rollback()
    print("[ERRO] Falha ao remover o overload antigo:", e)
finally:
    conn.close()
