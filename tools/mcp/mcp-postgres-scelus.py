# Servidor MCP (FastMCP) para o banco PostgreSQL dba_scelus (dev)
# Registro no projeto: ver .mcp.json na raiz do repositório.
import os
import json
import psycopg2
from psycopg2.extras import RealDictCursor
from mcp.server.fastmcp import FastMCP
from dotenv import load_dotenv

load_dotenv()

mcp = FastMCP("postgres-scelus-mcp")

DB_HOST = os.getenv("POSTGRES_HOST", "posdleg.tjma.jus.br")
DB_PORT = os.getenv("POSTGRES_PORT", "5432")
DB_NAME = os.getenv("POSTGRES_DB", "dba_scelus")
DB_USER = os.getenv("POSTGRES_USER", "dba_scelus")
DB_PASS = os.getenv("POSTGRES_PASSWORD", "dba_scelus")


def get_connection():
    return psycopg2.connect(
        host=DB_HOST,
        port=DB_PORT,
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASS,
    )


@mcp.tool()
def listar_tabelas_postgres(schema: str = "public"):
    """Lista todas as tabelas do banco no schema informado."""
    sql = """
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = %s
        ORDER BY table_name;
    """
    with get_connection() as conn:
        with conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute(sql, (schema,))
            return json.dumps(cursor.fetchall(), indent=2, default=str)


@mcp.tool()
def detalhar_tabela_postgres(tabela: str, schema: str = "public"):
    """Mostra as colunas, tipos de dados e chaves de uma tabela."""
    sql = """
        SELECT column_name, data_type, is_nullable, is_identity, column_default
        FROM information_schema.columns
        WHERE table_schema = %s AND table_name = %s
        ORDER BY ordinal_position;
    """
    with get_connection() as conn:
        with conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute(sql, (schema, tabela))
            return json.dumps(cursor.fetchall(), indent=2, default=str)


@mcp.tool()
def listar_funcoes_postgres(schema: str = "pkg_fato_ocorrido"):
    """Lista as funções de um schema/pacote (ex.: pkg_fato_ocorrido, pkg_litigancia)."""
    sql = """
        SELECT n.nspname AS schema, p.proname AS funcao,
               pg_get_function_arguments(p.oid) AS argumentos,
               pg_get_function_result(p.oid) AS retorno
        FROM pg_proc p JOIN pg_namespace n ON n.oid = p.pronamespace
        WHERE n.nspname = %s
        ORDER BY p.proname;
    """
    with get_connection() as conn:
        with conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute(sql, (schema,))
            return json.dumps(cursor.fetchall(), indent=2, default=str)


@mcp.tool()
def executar_query_postgres(sql: str):
    """Executa uma query SELECT (somente leitura) no PostgreSQL."""
    clean_sql = sql.strip().upper()
    if not (clean_sql.startswith("SELECT") or clean_sql.startswith("WITH")):
        return "Error: Apenas consultas SELECT são permitidas por segurança."

    with get_connection() as conn:
        with conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute(sql)
            return json.dumps(cursor.fetchall(), indent=2, default=str)


if __name__ == "__main__":
    mcp.run()
