FROM amazoncorretto:11-alpine-jdk
COPY payment-backend-service/target/payment-backend-service-1.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
