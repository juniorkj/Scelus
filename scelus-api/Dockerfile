# ── Stage 1: Build ────────────────────────────────────────────────────────────
# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

COPY pom.xml ./
COPY src ./src

# A tjma-infra-spring já está publicada no Nexus (br.jus.tjma.infra:tjma-infra-spring),
# então o build resolve tudo via repositório remoto — não precisa mais compilar a infra
# localmente. As credenciais de leitura do Nexus são passadas como build secret (nunca
# ficam em uma camada da imagem): docker build --secret id=maven_settings,src=settings.xml
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn package -DskipTests -B

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S scelus && adduser -S scelus -G scelus

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

RUN chown scelus:scelus app.jar
USER scelus

ENV SPRING_PROFILES_ACTIVE=local
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV TJMA_AMBIENTE=DESENVOLVIMENTO

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q -O /dev/null http://localhost:8080/scelus-api/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
