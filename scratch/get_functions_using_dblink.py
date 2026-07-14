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
        # Busca TODAS as funções normais (não agregadas) que usam a palavra 'dblink'
        query = """
        SELECT n.nspname as schema, p.proname as function_name, pg_get_function_arguments(p.oid) as arguments
        FROM pg_proc p
        JOIN pg_namespace n ON p.pronamespace = n.oid
        WHERE p.prokind = 'f' 
          AND pg_get_functiondef(p.oid) ILIKE '%dblink%';
        """
        cursor.execute(query)
        rows = cursor.fetchall()
        print(f"Total de funções usando dblink encontradas: {len(rows)}")
        for row in rows:
            print(f" - {row['schema']}.{row['function_name']}({row['arguments']})")
except Exception as e:
    print("Erro:", e)
finally:
    conn.close()
