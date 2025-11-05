# 声明目标平台
ARG TARGETPLATFORM=linux/amd64

# 第一阶段：构建阶段（使用JDK）
FROM --platform=$TARGETPLATFORM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段（使用JRE）
FROM --platform=$TARGETPLATFORM eclipse-temurin:17-jre
WORKDIR /app

# 复制构建好的jar包
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8084

# 运行应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
