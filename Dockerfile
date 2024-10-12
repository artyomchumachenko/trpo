# Используем официальный базовый образ OpenJDK 17
FROM openjdk:17-jdk

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем скомпилированный JAR файл в контейнер
COPY target/trpo-0.0.1-SNAPSHOT.jar /app/trpo-java-backend.jar

# Указываем команду для запуска JAR файла
ENTRYPOINT ["java", "-jar", "/app/trpo-java-backend.jar"]

# Указываем порт, который будет использоваться приложением
EXPOSE 8080
