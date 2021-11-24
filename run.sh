#!/bin/bash

IMAGE=kiv-pia-silhavyj_app

docker-compose down
mvn clean package -Dmaven.test.skip
if [[ $(docker images -q $IMAGE | wc -l) -eq 1 ]]; then
  docker image rmi $IMAGE
fi
docker-compose up