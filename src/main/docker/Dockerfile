FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD sys-spring-boot-starter.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
