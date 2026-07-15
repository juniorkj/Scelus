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
        # Procurando se as testemunhas estão cadastradas na tb_processo_parte em algum processo
        ids = "898104, 2044038, 2987151, 76881"
        q = f"""
        SELECT * FROM dblink('pje', '
            SELECT id_processo_parte, id_processo_trf, id_pessoa, id_tipo_parte, in_participacao, in_situacao
            FROM client.tb_processo_parte
            WHERE id_pessoa IN ({ids})
        ') AS t(id_processo_parte bigint, id_processo_trf integer, id_pessoa integer, id_tipo_parte integer, in_participacao text, in_situacao text);
        """
        cursor.execute(q)
        rows = cursor.fetchall()
        print("Vínculos das testemunhas na tb_processo_parte:")
        for r in rows:
            print(dict(r))
except Exception as e:
    print("Erro:", e)
finally:
    conn.close()
