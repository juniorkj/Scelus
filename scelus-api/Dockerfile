# ── Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S scelus && adduser -S scelus -G scelus

WORKDIR /app

# Copia o JAR já compilado localmente
COPY target/*.jar app.jar

RUN chown scelus:scelus app.jar
USER scelus

ENV SPRING_PROFILES_ACTIVE=local
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV TJMA_AMBIENTE=DESENVOLVIMENTO

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q -O /dev/null http://localhost:8080/scelus-api/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
