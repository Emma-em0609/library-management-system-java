# === Етап 1: Збірка ===
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Копіюємо pom.xml окремо — для кешування залежностей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копіюємо весь вихідний код
COPY src ./src

# Збираємо без тестів (тести потребують БД, яка ще не запущена)
RUN mvn clean package -DskipTests -B

# === Етап 2: Запуск ===
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копіюємо зібраний JAR з першого етапу
COPY --from=builder /app/target/library-0.0.1-SNAPSHOT.jar app.jar

# Порт, який Spring слухає (server.port=3000)
EXPOSE 3000

ENTRYPOINT ["java", "-jar", "app.jar"]