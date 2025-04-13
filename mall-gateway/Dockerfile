# 第一阶段：使用 Maven 构建项目
FROM maven:3.8.6-jdk-8 AS builder

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY pom.xml .
COPY src ./src

# 构建项目（跳过测试）
RUN mvn clean package -DskipTests

# 第二阶段：使用最小化的 JRE 运行环境
FROM openjdk:8-jre-alpine

# 设置工作目录
WORKDIR /app

# 从构建阶段复制生成的 jar 文件
COPY --from=builder /app/target/*.jar app.jar

# 设置 JVM 参数（可根据需要调整）
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]