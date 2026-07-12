# Servidor MCP (FastMCP) para a base Oracle do Sentinela (DBA_SENTINELA — dev)
# Registro no projeto: ver .mcp.json na raiz do repositório.
import os
import json
import oracledb
from mcp.server.fastmcp import FastMCP
from dotenv import load_dotenv

load_dotenv()

mcp = FastMCP("oracle-sentinela-mcp")

USER = os.getenv("ORACLE_USER", "dba_sentinela")
PASSWORD = os.getenv("ORACLE_PASSWORD", "dba_sentinela")
DSN = os.getenv("ORACLE_DSN", "oradpjma:1521/pjma")
THICK_MODE = os.getenv("ORACLE_THICK_MODE", "false").lower() == "true"
CLIENT_PATH = os.getenv("ORACLE_CLIENT_PATH")


def get_connection():
    if THICK_MODE:
        if CLIENT_PATH:
            oracledb.init_oracle_client(lib_dir=CLIENT_PATH)
        else:
            oracledb.init_oracle_client()
    return oracledb.connect(user=USER, password=PASSWORD, dsn=DSN)


@mcp.tool()
def consultar_permissoes(usuario_id: str, sistema_id: str = "scelus"):
    """Consulta permissões de um usuário no sistema informado no Sentinela."""
    sql = """
        SELECT DISTINCT go.STR_OBJETO_ID, go.IND_LEITURA, go.IND_INCLUSAO,
                        go.IND_ATUALIZACAO, go.IND_EXCLUSAO
        FROM DBA_SENTINELA.VW_COMPONENTE_GRUPO cg
        JOIN dba_sentinela.vw_grupo_objeto go ON go.INT_GRUPO_USUARIO_ID = cg.INT_GRUPO_USUARIO_ID
        AND go.str_sistema_id = cg.STR_SISTEMA_ID
        WHERE cg.STR_SISTEMA_ID = :sistema_id
        AND cg.STR_USUARIO_ID = :usuario_id
        AND cg.IND_USUARIO_ATIVO_GRUPO = 'S'
    """
    with get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(sql, sistema_id=sistema_id.upper(), usuario_id=usuario_id)
            columns = [col[0] for col in cursor.description]
            rows = [dict(zip(columns, row)) for row in cursor.fetchall()]
            return json.dumps(rows, indent=2, default=str)


@mcp.tool()
def listar_grupos_usuario(usuario_id: str, sistema_id: str = "scelus"):
    """Consulta os grupos associados a um usuário para o sistema informado."""
    sql = """
        SELECT gu.STR_GRUPO_USUARIO, gu.IND_ADMINISTRADOR
        FROM DBA_SENTINELA.VW_GRUPO_USUARIO gu
        JOIN DBA_SENTINELA.VW_COMPONENTE_GRUPO cg ON cg.INT_GRUPO_USUARIO_ID = gu.INT_GRUPO_USUARIO_ID
        AND cg.STR_SISTEMA_ID = gu.STR_SISTEMA_ID
        WHERE gu.STR_SISTEMA_ID = :sistema_id
        AND cg.STR_USUARIO_ID = :usuario_id
        AND cg.IND_USUARIO_ATIVO_GRUPO = 'S'
    """
    with get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute(sql, sistema_id=sistema_id.upper(), usuario_id=usuario_id)
            columns = [col[0] for col in cursor.description]
            rows = [dict(zip(columns, row)) for row in cursor.fetchall()]
            return json.dumps(rows, indent=2, default=str)


if __name__ == "__main__":
    mcp.run()
