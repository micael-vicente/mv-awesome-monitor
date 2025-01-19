FROM eclipse-temurin:21-jre-alpine

RUN mkdir /opt/app

COPY target/mv-awesome-monitor-*.jar /opt/app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/app/app.jar"]