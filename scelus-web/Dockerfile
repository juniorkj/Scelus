# ── Stage 1: Build ────────────────────────────────────────────────────────────
# A @tjma/angular-21 já está publicada no Nexus TJMA (npm-public), então o build
# não precisa mais de contexto multi-projeto nem de clonar a infra separadamente.
FROM node:22-alpine AS build

WORKDIR /app

# Copiar package.json, package-lock.json e .npmrc primeiro (cache de layers)
COPY package*.json .npmrc ./
RUN npm ci && rm .npmrc

COPY . .
RUN npm run build:prod

# ── Stage 2: Runtime Nginx ───────────────────────────────────────────────────
FROM nginx:alpine AS runtime

COPY --from=build /app/dist/scelus-web/browser /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
