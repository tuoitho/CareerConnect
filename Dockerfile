# Sử dụng image OpenJDK có Maven với JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file pom.xml và tải dependency trước để tối ưu cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Sao chép toàn bộ source code và build
COPY src ./src
RUN mvn clean package -DskipTests

# Sử dụng image nhẹ hơn để chạy ứng dụng với JDK 21
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Sao chép file JAR từ bước build
COPY --from=build /app/target/*.jar app.jar

# Chạy ứng dụng
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
