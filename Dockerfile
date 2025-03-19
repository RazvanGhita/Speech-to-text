# Use the official Maven image as parent image
FROM maven:3.8.5-openjdk-17-slim

# Set the working directory. If it doesn't exists, it'll be created
WORKDIR /app

# Define the env variable `PORT`
ENV PORT 8080

# Expose the port 8080
EXPOSE ${PORT}

# Copy all files from current folder
# inside our image in the folder `/app`
COPY . /app

# Generate Jar file
RUN mvn clean install -DskipTests

RUN cp /app/target/*.jar .
RUN mv *.jar app.jar
RUN apt-get update && apt-get install ffmpeg -y

#Add ENV variable to stop container from exiting
ENV CI=true

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]