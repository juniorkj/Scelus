import psycopg2
from psycopg2.extras import RealDictCursor

def run():
    try:
        conn = psycopg2.connect(
            host="posdleg.tjma.jus.br",
            port="5432",
            database="dba_scelus",
            user="dba_scelus",
            password="dba_scelus"
        )
        print("[OK] Conectado.")
        
        # Consultando a lista de tabelas/views do schema tjma no PG remoto
        sql = """
            SELECT * FROM dblink(
                'pje',
                'SELECT schemaname, tablename 
                 FROM pg_tables 
                 WHERE schemaname = ''tjma'' 
                 OR tablename LIKE ''%processo%'''
            ) AS t(schemaname varchar, tablename varchar);
        """
        
        with conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute(sql)
            rows = cursor.fetchall()
            print("\n--- Tabelas/Views no Banco Remoto ---")
            for r in rows:
                print(f"- {r['schemaname']}.{r['tablename']}")
                
        conn.close()
    except Exception as e:
        print("Erro:", e)

if __name__ == "__main__":
    run()
