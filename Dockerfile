FROM eclipse-temurin:11-jre-jammy

WORKDIR /examples

COPY ./build/install/examples/ .

# Expose the port your application listens on (if applicable)
#EXPOSE 8080

# Define the entrypoint to run your application
ENTRYPOINT ["sh", "-c", "exec /examples/bin/examples"]