FROM maven:3.9.4-eclipse-temurin-21 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app

# УСТАНОВКА FFMPEG в финальном образе
RUN apt-get update && apt-get install -y ffmpeg

COPY --from=builder /app/target/telegram-openai-bot-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]