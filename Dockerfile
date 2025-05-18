FROM maven:3.9.4-eclipse-temurin-21 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app

# УСТАНОВКА FFMPEG и YT-DLP в финальном образе
RUN apt-get update && \
    apt-get install -y ffmpeg curl python3 && \
    curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o /usr/local/bin/yt-dlp && \
    chmod +x /usr/local/bin/yt-dlp

ENV JAVA_TOOL_OPTIONS="-Xmx400m"

COPY --from=builder /app/target/telegram-openai-bot-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]