#!/bin/bash
# =============================================================================
# Scelus — Script de Deploy (DEV / Homol / Prod)
# Execute no servidor de destino.
# Uso: ./deploy.sh dev | homol | prod
# =============================================================================

function show_help() {
    echo "Uso: ./deploy.sh [ambiente]"
    echo "Ambientes suportados:"
    echo "  dev   → Deploy no ambiente de Desenvolvimento (branch: develop)"
    echo "  homol → Deploy no ambiente de Homologação    (branch: develop)"
    echo "  prod  → Deploy no ambiente de Produção       (branch: main)"
    exit 1
}

if [ -z "${1:-}" ]; then
    echo "❌ Erro: Ambiente não informado."
    show_help
fi

ENV=$(echo "$1" | tr '[:upper:]' '[:lower:]')

case "$ENV" in
    dev|homol) BRANCH="develop" ;;
    prod)      BRANCH="main"    ;;
    *)
        echo "❌ Erro: Ambiente '$ENV' inválido."
        show_help
        ;;
esac

echo "🚀 Iniciando deploy Scelus — ambiente: $ENV"
echo "📌 Branch alvo: $BRANCH"
echo "─────────────────────────────────────────────────────────────────"

# 1. Navega para a raiz do projeto
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_ROOT" || { echo "❌ Falha ao acessar $PROJECT_ROOT"; exit 1; }

# 2. Atualização via Git
echo ""
echo "📦 Baixando atualizações do Git..."
git config --global --add safe.directory "$(pwd)" 2>/dev/null || true
git fetch origin
git checkout "$BRANCH"
git pull origin "$BRANCH"

# 3. Variáveis de Ambiente
echo ""
if [ "$ENV" == "prod" ]; then
    if [ -f "variables.env" ]; then
        echo "⚙️  Produção: mantendo variables.env já configurado no servidor."
    elif [ -f "variables.prod.env" ]; then
        echo "⚠️  variables.env não encontrado. Criando a partir de variables.prod.env."
        echo "    ⚠️  Configure as senhas reais antes de subir os containers!"
        cp "variables.prod.env" "variables.env"
    else
        echo "❌ Erro: Nem variables.env nem variables.prod.env foram encontrados."
        exit 1
    fi
else
    if [ -f "variables.$ENV.env" ]; then
        echo "⚙️  Configurando variables.env para o ambiente: $ENV"
        cp "variables.$ENV.env" "variables.env"
    elif [ -f "variables.env" ]; then
        echo "⚠️  Sem variables.$ENV.env — usando variables.env existente no servidor."
    else
        echo "❌ Erro: Nem variables.env nem variables.$ENV.env encontrados."
        exit 1
    fi
fi

# 4. Detecção do comando Docker Compose
echo ""
echo "🔍 Detectando docker compose..."
if [ -f "/opt/tjma/docker-compose-linux" ]; then
    DC="/opt/tjma/docker-compose-linux"
elif docker compose version > /dev/null 2>&1; then
    DC="docker compose"
elif command -v docker-compose > /dev/null 2>&1; then
    DC="docker-compose"
elif [ -f "/usr/local/bin/docker-compose" ]; then
    DC="/usr/local/bin/docker-compose"
else
    echo "❌ Erro: docker compose não encontrado."
    exit 1
fi
echo "   Usando: $DC"

# 5. Pull das imagens do Registry
echo ""
echo "🚚 Baixando imagens atualizadas do registry..."
$DC pull
if [ $? -ne 0 ]; then
    echo "❌ Erro ao puxar imagens. Verifique o login: docker login registro-ops.tjma.jus.br"
    exit 1
fi

# 6. Sobe os containers
echo ""
echo "🐳 Subindo containers..."
$DC up -d
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Containers atualizados com sucesso — ambiente: $ENV"
    echo ""
    $DC ps
else
    echo "❌ Falha ao subir containers. Verifique os logs:"
    echo "   docker logs scelus-api --tail 50"
    echo "   docker logs scelus-web --tail 50"
    exit 1
fi

# 7. Limpeza de imagens antigas
echo ""
echo "🧹 Removendo imagens sem tag..."
docker image prune -f

echo ""
echo "🎉 Deploy ('$ENV') finalizado!"
