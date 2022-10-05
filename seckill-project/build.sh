#!/bin/bash

# 本地编译代码并打包
mvn compile package -DskipTests=true

# 构建最新的image
docker build  -t seckill_img:latest .

# 停止可能在运行的container
docker stop seckill

# 删除可能存在的container
docker rm seckill

# The --rm option means to remove the container once it exits/stops.
# The -d flag means to start the container detached (in the background).
docker run -d -p 8080:8080 -p 6379:6379 --name seckill seckill_img