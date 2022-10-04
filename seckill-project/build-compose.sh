#!/bin/bash
mvn compile package -DskipTests=true

docker build  -t seckill_img:latest .

docker-compose stop

# The --rm option means to remove the container once it exits/stops.
# The -d flag means to start the container detached (in the background).
docker-compose up -d