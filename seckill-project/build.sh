#!/bin/bash
mvn compile package -DskipTests=true

docker build  -t seckill_img:latest .

docker stop seckill

docker rm seckill

# The --rm option means to remove the container once it exits/stops.
# The -d flag means to start the container detached (in the background).
docker run -d -p 8080:8080 -p 6379:6379 --name seckill seckill_img