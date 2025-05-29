FROM maven:3.9.4-eclipse-temurin-21 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM ubuntu:22.04
WORKDIR /app

RUN apt-get update && \
    apt-get install -y ffmpeg curl python3 openjdk-21-jdk && \
    curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o /usr/local/bin/yt-dlp && \
    chmod +x /usr/local/bin/yt-dlp

ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV JAVA_TOOL_OPTIONS="-Xmx4096m"

COPY --from=builder /app/target/telegram-openai-bot-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]