FROM amazoncorretto:17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :rest:test
RUN ./gradlew :rest:bootJar -x test

FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/rest/build/libs/*.jar rest.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "rest.jar"]