FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/HansenApp_2-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]