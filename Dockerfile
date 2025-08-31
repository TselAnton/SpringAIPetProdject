FROM openjdk:21-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} opensearch-demo-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/opensearch-demo-0.0.1-SNAPSHOT.jar"]