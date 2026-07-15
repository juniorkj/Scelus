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
        q = """
        SELECT * FROM dblink('pje', '
            SELECT table_schema, table_name 
            FROM information_schema.columns 
            WHERE column_name = ''id_processo_trf''
        ') AS t(table_schema text, table_name text);
        """
        cursor.execute(q)
        tables = cursor.fetchall()
        print("Tabelas que possuem id_processo_trf no PJe:")
        for t in tables:
            print(f" - {t['table_schema']}.{t['table_name']}")
except Exception as e:
    print("Erro:", e)
finally:
    conn.close()
