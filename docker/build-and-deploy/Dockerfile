FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /examples

COPY . .

RUN ./gradlew build check installDist --no-daemon

FROM eclipse-temurin:11-jre-jammy

WORKDIR /examples

COPY --from=build /examples/build/install/examples/ .

# Expose the port your application listens on (if applicable)
#EXPOSE 8080

# Define the entrypoint to run your application
ENTRYPOINT ["sh", "-c", "exec ./bin/examples"]