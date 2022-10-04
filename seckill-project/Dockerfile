FROM openjdk:11
MAINTAINER Steve HU

COPY target/seckill-project-0.0.1-SNAPSHOT.jar /usr/local/jar/seckill-project-0.0.1-SNAPSHOT.jar
# 使用docker环境的配置
#CMD ["java", "-jar", "/usr/local/jar/seckill-project-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]
CMD ["java", "-jar", "/usr/local/jar/seckill-project-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker-compose"]
