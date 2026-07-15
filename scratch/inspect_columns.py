import psycopg2
from psycopg2.extras import RealDictCursor

conn = psycopg2.connect(
    host="posdleg.tjma.jus.br",
    port="5432",
    database="dba_scelus",
    user="dba_scelus",
    password="dba_scelus"
)

try:
    with conn.cursor(cursor_factory=RealDictCursor) as cursor:
        proc_id = 2789395  # ID do processo 0000680-60.2018.8.10.0111
        cursor.execute(f"SELECT * FROM pkg_processo.fn_processo_parte_pje_con(NULL, {proc_id});")
        partes = cursor.fetchall()
        if partes:
            print("Colunas disponíveis no retorno da função:")
            print(list(partes[0].keys()))
            print("\nExemplo completo de cada parte:")
            for p in partes:
                print(dict(p))
        else:
            print("Nenhuma parte encontrada.")
except Exception as e:
    print("Erro:", e)
finally:
    conn.close()
