FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app
COPY . .

RUN ./gradlew build -x test -x integrationTest

RUN ls -l build/libs/

# Используем правильное имя файла - job4j_devops-version.jar
RUN jar xf build/libs/job4j_devops-v5.0.1.jar

RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    build/libs/job4j_devops-v5.0.1.jar > deps.info

RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /slim-jre

FROM debian:bookworm-slim

ENV JAVA_HOME=/usr/java/jdk21
ENV PATH=$JAVA_HOME/bin:$PATH

COPY --from=builder /slim-jre $JAVA_HOME
COPY --from=builder /app/build/libs/job4j_devops-v5.0.1.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]