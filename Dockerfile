FROM adoptopenjdk/openjdk11:latest
ARG JAR_FILE=target/*.jar
COPY images/* /images/
COPY ${JAR_FILE} /opt/app.jar
ENTRYPOINT ["java", "-jar" , "/opt/app.jar"]