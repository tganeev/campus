FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Копируем pom.xml и скачиваем зависимости (для кэширования)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN mvn clean package -DskipTests

# Этап запуска
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Копируем собранный jar из предыдущего этапа
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]