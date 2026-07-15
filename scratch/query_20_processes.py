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
        
        # Consultando os processos e assuntos criminais usando o dblink 'pje'
        # Vamos fazer um JOIN simples no banco remoto e buscar 30 processos
        sql = """
            SELECT DISTINCT nr_processo 
            FROM dblink(
                'pje',
                'SELECT p.nr_processo 
                 FROM core.tb_processo p 
                 INNER JOIN client.tb_processo_assunto pa ON pa.id_processo = p.id_processo
                 WHERE p.nr_processo IS NOT NULL 
                 AND pa.id_assunto_trf IN (
                     SELECT id_assunto_trf 
                     FROM pje.tb_assunto_trf 
                     WHERE cd_assunto IN (14234, 3423, 3373, 3356, 3360, 5555) -- Assuntos criminais/Maria da Penha
                 )
                 AND ROWNUM <= 40'
            ) AS t(nr_processo varchar);
        """
        
        # Se a query anterior falhar por conta do schema 'pje' da tabela tb_assunto_trf, 
        # tentamos uma query mais geral que traga processos gerais que estejam cadastrados:
        sql_fallback = """
            SELECT DISTINCT nr_processo 
            FROM dblink(
                'pje',
                'SELECT nr_processo 
                 FROM core.tb_processo 
                 WHERE nr_processo IS NOT NULL 
                 AND ROWNUM <= 30'
            ) AS t(nr_processo varchar);
        """
        
        with conn.cursor(cursor_factory=RealDictCursor) as cursor:
            try:
                cursor.execute(sql)
                rows = cursor.fetchall()
                print("\n--- Processos Criminais via dblink ---")
                for r in rows:
                    print(r['nr_processo'])
            except Exception as e:
                print("Tentando fallback sem filtro de assunto...")
                conn.rollback()
                cursor.execute(sql_fallback)
                rows = cursor.fetchall()
                print("\n--- Processos Gerais via dblink ---")
                for r in rows:
                    print(r['nr_processo'])
                
        conn.close()
    except Exception as e:
        print("Erro:", e)

if __name__ == "__main__":
    run()
