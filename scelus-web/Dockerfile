# ── Stage 1: Runtime Nginx ───────────────────────────────────────────────────
# Como o build estático já é gerado localmente pelo script de deploy/desenvolvedor
# com 'npm run build:prod', o Dockerfile apenas empacota a pasta dist final no Nginx.
FROM nginx:alpine AS runtime

# Copia os arquivos gerados no build local direto para o Nginx
COPY dist/scelus-web/browser /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
