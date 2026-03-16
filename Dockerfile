FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw && ./mvnw -q -DskipTests package

RUN set -eux; \
    JARS="$(find target -maxdepth 1 -type f -name '*.jar' ! -name 'original-*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar')"; \
    COUNT="$(printf '%s\n' "${JARS}" | sed '/^$/d' | wc -l | tr -d ' ')"; \
    if [ "${COUNT}" -ne 1 ]; then \
      echo "Expected exactly one runnable JAR in target/, found ${COUNT}" >&2; \
      printf '%s\n' "${JARS}" >&2; \
      exit 1; \
    fi; \
    cp "${JARS}" /tmp/app.jar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring --create-home spring

COPY --from=build /tmp/app.jar /app/app.jar

ENV SERVER_PORT=8080
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

USER spring:spring

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${SERVER_PORT} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar /app/app.jar"]
