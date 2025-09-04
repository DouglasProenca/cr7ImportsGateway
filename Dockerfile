FROM openjdk:8
WORKDIR /app
COPY target/cr7ImportsGateway.jar /app/cr7ImportsGateway.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "cr7ImportsGateway.jar"]