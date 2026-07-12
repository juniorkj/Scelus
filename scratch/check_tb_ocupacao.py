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
        cursor.execute("SELECT COUNT(*) FROM public.tb_ocupacao;")
        print("Total de ocupações:", cursor.fetchone())
        
        cursor.execute("SELECT * FROM public.tb_ocupacao LIMIT 5;")
        print("Amostra de ocupações:")
        for row in cursor.fetchall():
            print(row)
except Exception as e:
    print("Erro:", e)
