FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the .jar file from the builder stage
COPY /build/libs/load-generator-adapter.jar /app

# Expose the application port
EXPOSE 8087

# Run the application
ENTRYPOINT ["java","-jar","/app/load-generator-adapter.jar"]