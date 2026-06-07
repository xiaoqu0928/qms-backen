# 用和你项目匹配的 JDK 17 基础镜像
FROM openjdk:17-jdk-slim

# 把本地 target 目录下的 jar 包复制到容器里，命名为 app.jar
COPY target/query-management-system-1.0.0.jar app.jar

# 容器启动时执行的命令，和你本地启动方式一致
ENTRYPOINT ["java", "-jar", "app.jar"]

# 暴露 8080 端口，和你的 SpringBoot 配置一致
EXPOSE 8080