#!/bin/bash

# This Script is used for automatic Docker Image build.
# If you want to use it, set the Enviroment Variables to your
# Own Docker credentials

echo "Build and Push Docker"

#cowsay "Pull last changes from Git"

#git pull

cowsay "Build with Gradle"

./gradlew build

cowsay "Build and Push Docker Image"

docker build -t $DOCKERHUB_USERNAME/hc3-core:latest .

docker login --username $DOCKERHUB_USERNAME --password $DOCKERHUB_PASSWORD

docker push $DOCKERHUB_USERNAME/hc3-core:latest
