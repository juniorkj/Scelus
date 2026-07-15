import psycopg2

conn = psycopg2.connect(
    host="posdleg.tjma.jus.br",
    port="5432",
    database="dba_scelus",
    user="dba_scelus",
    password="dba_scelus"
)

try:
    with conn.cursor() as cursor:
        cursor.execute("SELECT pg_get_functiondef('pkg_processo.fn_processo_parte_pje_con'::regproc);")
        def_row = cursor.fetchone()
        if def_row:
            print(def_row[0])
        else:
            print("Função não encontrada.")
except Exception as e:
    print("Erro:", e)
finally:
    conn.close()
