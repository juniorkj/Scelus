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
        # Busca no acl.tb_usuario_login para ver se as testemunhas existem no PJe
        names = ["DOMINGOS FRANCISCO DA CUNHA NETO", "DORACI OLIVEIRA MARTINS", "EMERSON BORGES BELFORT"]
        for name in names:
            q = f"""
            SELECT * FROM dblink('pje', '
                SELECT id_usuario, ds_nome 
                FROM acl.tb_usuario_login 
                WHERE ds_nome LIKE ''%{name}%''
            ') AS t(id_usuario integer, ds_nome text);
            """
            cursor.execute(q)
            rows = cursor.fetchall()
            print(f"Resultados para {name}:")
            for r in rows:
                print(r)
except Exception as e:
    print("Erro:", e)
finally:
    conn.close()
