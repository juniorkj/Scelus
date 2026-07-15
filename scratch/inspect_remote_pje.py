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
            FROM information_schema.tables 
            WHERE table_name LIKE ''%represent%'' OR table_name LIKE ''%terceir%''
        ') AS t(table_schema text, table_name text);
        """
        cursor.execute(q)
        tables = cursor.fetchall()
        for t in tables:
            print(f"{t['table_schema']}.{t['table_name']}")
except Exception as e:
    print("Erro:", e)
finally:
    conn.close()
