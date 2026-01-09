FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app
COPY . .

RUN ./gradlew build -x test -x integrationTest

RUN ls -l build/libs/

# Используем правильное имя файла - app-1.0.0.jar
RUN jar xf build/libs/app-1.0.0.jar

RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    build/libs/app-1.0.0.jar > deps.info

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
COPY --from=builder /app/build/libs/app-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
