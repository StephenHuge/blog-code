# 从mysql基础镜像进入
FROM mysql

# 设置terminal进入docker后的默认目录
WORKDIR /usr/bin

# Oracle linux 8 没有安装yum，但是可以使用microdnf进行安装
RUN microdnf install -y vim

# 把开始脚本放到这个文件夹里，docker容器启动后就会执行，具体参见 https://github.com/docker-library/docs/tree/master/mysql#initializing-a-fresh-instance
COPY sql/init.sql /docker-entrypoint-initdb.d/
# 将配置文件放到指定目录


# 声明日志数据卷
VOLUME /var/log/mysql
## 声明数据数据卷
VOLUME /var/lib/mysql
## 本地映射配置数据卷
VOLUME /etc/mysql/conf.d

# 暴露端口为3306
EXPOSE 3306

# [MYSQL_RANDOM_ROOT_PASSWORD, MYSQL_ROOT_PASSWORD, MYSQL_ALLOW_EMPTY_PASSWORD] MySQL配置3选1

# 如果MYSQL_ROOT_PASSWORD, MYSQL_ALLOW_EMPTY_PASSWORD没有配置则默认为true。如果为true，
# mysql会自动生成一个密码，会打印在控制台，格式是： [GENERATED ROOT PASSWORD: .....]
#ENV MYSQL_RANDOM_ROOT_PASSWORD=true
# 设置MySQL root的密码
ENV MYSQL_ROOT_PASSWORD=123456
# 为true的话，root用户的密码就是空
#ENV MYSQL_ALLOW_EMPTY_PASSWORD=true

#设置容器启动时执行的命令，这里有个坑，直接用这条会导致mysql无法正常连接
#CMD ["sh", "/mysql/setup_test.sh"]
CMD ["mysqld"]
