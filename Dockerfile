FROM openjdk:17
EXPOSE 8085
COPY target/user-service-docker.jar user-service-docker.jar

ENTRYPOINT ["java", "-jar","/user-service-docker.jar"]