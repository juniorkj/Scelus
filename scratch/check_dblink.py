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
        cursor.execute("SELECT * FROM pg_foreign_server;")
        print("Foreign servers:")
        for row in cursor.fetchall():
            print(row)
            
        cursor.execute("SELECT * FROM pg_user_mappings;")
        print("\nUser mappings:")
        for row in cursor.fetchall():
            print(row)
            
        # Tenta desconectar e reconectar o dblink 'pje'
        print("\nTentando forçar dblink_disconnect/dblink_connect...")
        try:
            cursor.execute("SELECT dblink_disconnect('pje');")
            print("Desconectou dblink pje")
        except Exception as e:
            print("Não pôde desconectar (talvez não estivesse ativo):", e)
            
except Exception as e:
    print("Erro:", e)
