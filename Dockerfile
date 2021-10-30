FROM openjdk:8
ADD target/springboot-mongodb-docker.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]