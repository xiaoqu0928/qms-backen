# 用 Java 17 基础镜像
FROM openjdk:17-jdk-slim

# 直接复制你本地编译好的 Jar 包
COPY target/query-management-system-1.0.0.jar app.jar

# 启动 SpringBoot 服务
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 暴露 8080 端口
EXPOSE 8080