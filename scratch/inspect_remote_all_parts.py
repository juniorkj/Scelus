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
        proc_id = 2789395  # ID do processo 0000680-60.2018.8.10.0111
        q = f"""
        SELECT * FROM dblink('pje', '
            SELECT tpp.id_processo_parte, tpp.in_participacao, tp.ds_tipo_parte, tul.ds_nome, tpp.id_pessoa
            FROM client.tb_processo_parte tpp
            INNER JOIN client.tb_tipo_parte tp ON tpp.id_tipo_parte = tp.id_tipo_parte
            INNER JOIN acl.tb_usuario_login tul ON tul.id_usuario = tpp.id_pessoa
            WHERE tpp.id_processo_trf = {proc_id}
        ') AS t(id_processo_parte bigint, in_participacao text, ds_tipo_parte text, ds_nome text, id_pessoa integer);
        """
        cursor.execute(q)
        rows = cursor.fetchall()
        print(f"Total de partes na tb_processo_parte: {len(rows)}")
        for r in rows:
            print(f"Nome: {r['ds_nome']} | Participacao: {r['in_participacao']} | Tipo: {r['ds_tipo_parte']} | ID Pessoa: {r['id_pessoa']}")
except Exception as e:
    print("Erro:", e)
finally:
    conn.close()
