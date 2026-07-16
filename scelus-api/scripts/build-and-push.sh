#!/bin/bash
# =============================================================================
# Scelus — Script de Build e Push das Imagens Docker para o Registry TJMA
# Execute localmente ANTES de fazer o deploy no servidor.
# Pré-requisitos: Maven, Node/npm, Docker, login no registry TJMA
# =============================================================================

set -euo pipefail

REGISTRY="registro-ops.tjma.jus.br/tjma/scelus"
VERSION="${1:-latest}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
API_ROOT="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$API_ROOT")"

echo "🔨 Scelus — Build & Push"
echo "   Registry : $REGISTRY"
echo "   Versão   : $VERSION"
echo "─────────────────────────────────────────────────────────────────"

# ── 1. Build do Backend (Maven) ───────────────────────────────────────
echo ""
echo "📦 [1/4] Compilando scelus-api com Maven..."
cd "$API_ROOT"
mvn package -DskipTests -B -s ci/settings.xml

# ── 2. Docker Build da API ────────────────────────────────────────────
echo ""
echo "🐳 [2/4] Build da imagem scelus-api..."

# Cria settings temporário para o --secret do Docker BuildKit
SETTINGS_TMP=$(mktemp /tmp/scelus-settings-XXXX.xml)
trap 'rm -f "$SETTINGS_TMP"' EXIT

cat > "$SETTINGS_TMP" <<EOF
<settings>
  <servers>
    <server>
      <id>tjma-public</id>
      <username>${TJMA_MAVEN_CREDS_USR:?Defina a variável TJMA_MAVEN_CREDS_USR}</username>
      <password>${TJMA_MAVEN_CREDS_PWD:?Defina a variável TJMA_MAVEN_CREDS_PWD}</password>
    </server>
  </servers>
</settings>
EOF

DOCKER_BUILDKIT=1 docker build \
  --secret id=maven_settings,src="$SETTINGS_TMP" \
  -t "$REGISTRY/scelus-api:$VERSION" \
  -t "$REGISTRY/scelus-api:latest" \
  .

# ── 3. Build do Frontend (Angular) ───────────────────────────────────
echo ""
echo "📦 [3/4] Compilando scelus-web com npm..."
cd "$PROJECT_ROOT/scelus-web"
npm ci
npm run build:prod

# ── 4. Docker Build do Frontend ───────────────────────────────────────
echo ""
echo "🐳 [4/4] Build da imagem scelus-web..."
docker build \
  -t "$REGISTRY/scelus-web:$VERSION" \
  -t "$REGISTRY/scelus-web:latest" \
  .

# ── Push ──────────────────────────────────────────────────────────────
echo ""
echo "🚀 Publicando imagens no registry..."

docker push "$REGISTRY/scelus-api:$VERSION"
docker push "$REGISTRY/scelus-api:latest"

docker push "$REGISTRY/scelus-web:$VERSION"
docker push "$REGISTRY/scelus-web:latest"

echo ""
echo "✅ Build e push concluídos com sucesso!"
echo "   scelus-api : $REGISTRY/scelus-api:$VERSION"
echo "   scelus-web : $REGISTRY/scelus-web:$VERSION"
echo ""
echo "Próximo passo: execute no servidor DEV → ./scripts/deploy.sh dev"
