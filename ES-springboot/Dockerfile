# 使用 JDK 17 基础镜像
FROM eclipse-temurin:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制打包好的 jar 文件（注意：jar 文件名要和你实际的一致）
COPY target/ES-springboot-0.0.1-SNAPSHOT.jar app.jar

# 创建 uploads 目录
RUN mkdir -p /app/uploads

# 暴露后端端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]