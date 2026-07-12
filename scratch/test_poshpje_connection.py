import psycopg2

try:
    print("Tentando conectar diretamente em poshpje...")
    conn = psycopg2.connect(
        host="poshpje",
        port="5432",
        database="pje",
        user="dba_scelus",
        password="dba_scelus",
        connect_timeout=5
    )
    print("Conexão direta com poshpje realizada com SUCESSO!")
    conn.close()
except Exception as e:
    print("Erro ao conectar diretamente em poshpje:", e)
