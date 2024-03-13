FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon --exclude-task test

FROM eclipse-temurin:17-alpine

COPY --from=build /home/gradle/src/build/libs/practica-6-0.0.1-SNAPSHOT.jar /app/application.jar

ENV DB_NAME=mocking
ENV DB_USER='root'
ENV DB_PASSWORD='pass'

ENV REDIS_HOST=redis
ENV REDIS_PORT=6379

ENV PORT_APP=8080
EXPOSE ${PORT_APP}

# ENTRYPOINT ["java",  "-Dserver.port=${PORT_APP}", "-Dspring.datasource.username=${DB_USER}", "-Dspring.datasource.password=${DB_PASSWORD}", "-Djava.net.preferIPv4Stack=true", "-jar", "/app/application.jar"]
ENTRYPOINT ["java", "-Dserver.port=${PORT_APP}", "-Dspring.datasource.username=${DB_USER}", "-Dspring.datasource.password=${DB_PASSWORD}", "-Dspring.data.redis.host=${REDIS_HOST}", "-Dspring.data.redis.port=${REDIS_PORT}", "-Djava.net.preferIPv4Stack=true", "-jar", "/app/application.jar"]