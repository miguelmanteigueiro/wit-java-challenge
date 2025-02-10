FROM amazoncorretto:17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :calculator:test
RUN ./gradlew :calculator:bootJar -x test

FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/calculator/build/libs/*.jar calculator.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "calculator.jar"]