FROM eclipse-temurin:17-jdk
VOLUME /tmp
COPY build/libs/load-generator-adapter.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]