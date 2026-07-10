#!/usr/bin/env bash

# Setup Java 21 environment
export JAVA_HOME="C:\Program Files\Java\jdk-21"
export PATH="$JAVA_HOME/bin:$PATH"

# Load other environment variables
set -a
[ -f .env ] && source .env
set +a

# Clean and compile
mvn clean compile -DskipTests

# Run the application with explicit system ID variants to avoid SISTEMA_NAO_CONFIGURADO
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Did_sistema=scelus -Did-sistema=scelus -Did.sistema=scelus -Dsistema.id=scelus"
