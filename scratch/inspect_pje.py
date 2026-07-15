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
        
        # Consultando a lista de tabelas/views do owner TJMA no Oracle
        sql = """
            SELECT * FROM dblink(
                'pje',
                'SELECT object_name, object_type 
                 FROM all_objects 
                 WHERE owner = ''TJMA'' 
                 AND object_type IN (''TABLE'', ''VIEW'')
                 AND object_name LIKE ''%PROCESSO%'''
            ) AS t(object_name varchar, object_type varchar);
        """
        
        with conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute(sql)
            rows = cursor.fetchall()
            print("\n--- Tabelas/Views de Processo no Oracle (TJMA) ---")
            for r in rows:
                print(f"- {r['object_name']} ({r['object_type']})")
                
        conn.close()
    except Exception as e:
        print("Erro:", e)

if __name__ == "__main__":
    run()
