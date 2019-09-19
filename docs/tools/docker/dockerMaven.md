# Docker
> docker 常用功能笔记

- [安装](/tools/docker/dockerMaven?id=安装)
- [docker-安装-mysql](/tools/docker/dockerMaven?id=docker-安装-mysql)
- [docker-安装-redis](/tools/docker/dockerMaven?id=docker-安装-redis)
- [docker-安装-rabbitmq](/tools/docker/dockerMaven?id=docker-安装-rabbitmq)
- [docker-构建镜像（maven）](/tools/docker/dockerMaven?id=docker-构建镜像（maven）)
- [docker-构建镜像并上传的远程仓库](/tools/docker/dockerMaven?id=docker-构建镜像并上传的远程仓库)
- [docker-常用命令](/tools/docker/dockerMaven?id=docker-常用命令)

## 安装
> centos 版本安装

``` bash
# 移除旧的版本：
  $ sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-selinux \
                  docker-engine-selinux \
                  docker-engine

# 安装一些必要的系统工具：
  sudo yum install -y yum-utils device-mapper-persistent-data lvm2

# 添加软件源信息：
  sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

# 更新 yum 缓存：
  sudo yum makecache fast

# 安装 Docker-ce：
  sudo yum -y install docker-ce

# 启动 Docker 后台服务
  sudo systemctl start docker
```

## Docker 安装 mysql
> mysql docker 仓库地址： [https://hub.docker.com/_/mysql/](https://hub.docker.com/_/mysql/)

##### 1. 拉取MySQL镜像

``` bash
docker pull mysql
```

##### 2. 创建并启动一个MySQL容器

``` bash
docker run --name mysqlserver -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 -d mysql
```
?> `–name`：给新创建的容器命名，此处命名为mysqlserver <br>
`-e`：配置信息，此处配置mysql的root用户的登陆密码 <br>
`-p`：端口映射，表示在这个容器中使用3306端口(第二个)映射到本机的端口号也为3306(第一个) <br>
`-d`：成功启动容器后输出容器的完整ID <br>

## Docker 安装 redis
> redis docker 仓库地址： [https://hub.docker.com/_/redis](https://hub.docker.com/_/redis)

##### 1. 选择最新版latest

``` bash
docker pull redis:latest
```

##### 2. 创建容器并设置密码

``` bash
docker run --name redis -p 6379:6379 -d --restart=always redis:latest redis-server --appendonly yes --requirepass 'Hao123baidu'
```

?> `-p` 6379:6379 :将容器内端口映射到宿主机端口(右边映射到左边)<br>
`redis-server –appendonly yes` : 在容器执行redis-server启动命令，并打开redis持久化配置<br>
`requirepass “your passwd”` :设置认证密码<br>
`–restart=always` : 随docker启动而启动<br>

## Docker 安装 RabbitMQ
> RabbitMQ docker 仓库地址：[https://hub.docker.com/_/rabbitmq](https://hub.docker.com/_/rabbitmq)

##### 1. 拉取镜像

``` bash
docker search rabbitmq:management
docker pull rabbitmq:management
```

##### 2. 启动镜像（默认用户名密码）,默认guest 用户，密码也是 guest

``` bash
docker run -d --hostname my-rabbit --name rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management
```

##### 3. 启动镜像（设置用户名密码）

```bash
docker run -d --hostname my-rabbit --name rabbit -e RABBITMQ_DEFAULT_USER=user -e RABBITMQ_DEFAULT_PASS=password -p 15672:15672 -p 5672:5672 rabbitmq:3-management
```

##### 4. 完成后访问：http://localhost:15672/


## Docker 构建镜像（Maven）
> 此处介绍 使用Maven来构建Docker镜像并上传到私服仓库

##### 1. 开启docker远程API端口

``` bash
# 修改docker配置文件
  vi /usr/lib/systemd/system/docker.service

# 在ExecStart后面添加 -H unix:///var/run/docker.sock -H 0.0.0.0:2375

# 重新加载配置文件
  systemctl daemon-reload

# 重启docker
  systemctl restart docker
```

##### 2. 添加maven docker插件

``` xml
  <!-- docker 配置-->
  <plugin>
      <groupId>com.spotify</groupId>
      <artifactId>docker-maven-plugin</artifactId>
      <version>1.2.0</version>
      <configuration>
          <forceTags>true</forceTags>
          <imageName>grasp-spring</imageName>
          <baseImage>azul/zulu-openjdk:8</baseImage>
          <entryPoint>["java", "-jar", "/${project.build.finalName}.jar"]</entryPoint>
          <dockerHost>http://192.168.1.45:2375</dockerHost>
          <resources>
              <resource>
                  <targetPath>/</targetPath>
                  <directory>${project.build.directory}</directory>
                  <include>${project.build.finalName}.jar</include>
              </resource>
          </resources>
      </configuration>
  </plugin>
```

##### 3. 执行maven 打包命令

``` bash
  mvn clean package docker:build
```

## Docker 构建镜像并上传的远程仓库

##### 1. 创建docker远程仓库

> 去[docker hub](https://hub.docker.com)官网注册账号，并创建远程仓库

##### 2. 修改本地maven配置文件 settings.xml
添加docker hub账号和密码

``` xml
<servers>
  <server>
    <id>docker-hub</id>
    <username>你的DockerHub用户名</username>
    <password>你的DockerHub密码</password>
    <configuration>
      <email>你的DockerHub邮箱</email>
    </configuration>
  </server>
</servers>
```

##### 3. 项目pom.xml修改为如下：注意imageName的路径要和repo的路径一致

``` xml
  <plugin>
      <groupId>com.spotify</groupId>
      <artifactId>docker-maven-plugin</artifactId>
      <version>1.2.0</version>
      <configuration>
          <forceTags>true</forceTags>
          <imageName>zhangbiyu/grasp-spring</imageName>
          <baseImage>azul/zulu-openjdk:8</baseImage>
          <entryPoint>["java", "-jar", "/${project.build.finalName}.jar"]</entryPoint>
          <dockerHost>http://192.168.1.45:2375</dockerHost>
          <resources>
              <resource>
                  <targetPath>/</targetPath>
                  <directory>${project.build.directory}</directory>
                  <include>${project.build.finalName}.jar</include>
              </resource>
          </resources>
          <serverId>docker-hub</serverId>
          <registryUrl>zhangbiyu/grasp-spring</registryUrl>
      </configuration>
  </plugin>
```

##### 4. 执行打包命令

``` bash
  mvn clean package docker:build  docker:push
```

## Docker 常用命令
> 日常用到的命令，docker基础命令跳转 [runoob](http://www.runoob.com/docker/docker-command-manual.html)

``` bash
# 1. 进入docker容器内部
  docker exec -it xxx /bin/bash 

# 2.同步docker容器与宿主机时间
  docker cp /etc/localtime 8ea91b2f6274:/etc/

# 3.删除镜像为none的镜像
  docker rmi $(docker images | grep "none" | awk '{print $3}')

# 4.查看指定时间后的日志，只显示最后100行
  docker logs -f -t --since="2018-02-08" --tail=100 CONTAINER_ID


```
