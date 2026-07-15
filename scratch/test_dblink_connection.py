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
        print("1. Testando conexão crua via dblink 'pje'...")
        cursor.execute("SELECT * FROM dblink('pje', 'SELECT 1') AS t(val integer);")
        row = cursor.fetchone()
        print("   Sucesso no dblink cru! Resultado:", row)
        
        real_proc = '0000680-60.2018.8.10.0111'
        print(f"\n2. Testando função pkg_processo.fn_processo_pje_con com o processo {real_proc}...")
        cursor.execute(f"SELECT * FROM pkg_processo.fn_processo_pje_con('{real_proc}');")
        proc = cursor.fetchone()
        print("   Sucesso na busca de processo! Resultado:", proc)
        
        if proc and proc['int_processo_id']:
            proc_id = proc['int_processo_id']
            print(f"\n3. Testando função pkg_processo.fn_processo_parte_pje_con com int_processo_id = {proc_id}...")
            cursor.execute(f"SELECT * FROM pkg_processo.fn_processo_parte_pje_con(NULL, {proc_id});")
            partes = cursor.fetchall()
            print(f"   Sucesso na busca de partes! Retornou {len(partes)} registros:")
            for p in partes:
                print(f"     - Parte ID: {p['int_parte_id']} | Nome: {p['str_nome']} | Polo: {p['str_polo']}")
        else:
            print("\n3. Pulado teste de partes porque nenhum ID de processo foi retornado.")
            
except Exception as e:
    print("\n[ERRO] Falha no teste:", e)
finally:
    conn.close()
