#!/bin/bash

IMAGE=kiv-pia-silhavyj_app

docker-compose down
mvn clean package
if [[ $(docker images -q $IMAGE | wc -l) -eq 1 ]]; then
  docker image rmi --force $IMAGE
fi
docker-compose up