# Maven

> 记录一些maven遇到的问题

![](https://maven.apache.org/images/maven-logo-black-on-white.png ':size=250')

- [springboot-多环境打包](/tools/maven/maven?id=springboot-多环境打包)
- [maven-引入本地项目jar包](/tools/maven/maven?id=maven-引入本地项目jar包)

## Springboot 多环境打包
> 基于springboot多环境打包问题，可根据springboot多环境配置文件一起使用

##### 1. maven 添加多环境配置

``` xml
<profiles>
    <!-- 开发环境 -->
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>
    <!-- 生产环境 -->
    <profile>
        <id>pro</id>
        <activation>
            <activeByDefault>false</activeByDefault>
        </activation>
        <properties>
            <spring.profiles.active>pro</spring.profiles.active>
        </properties>
    </profile>
    <!-- 测试环境 -->
    <profile>
        <id>test</id>
        <activation>
            <activeByDefault>false</activeByDefault>
        </activation>
        <properties>
            <spring.profiles.active>test</spring.profiles.active>
        </properties>
    </profile>
</profiles>
```

##### 2. maven 打包设置

``` xml
<resources>
    <resource><!-- 扫描替换 -->
        <directory>${project.basedir}/src/main/resources</directory>
        <filtering>true</filtering>
    </resource>
</resources>
```

##### 3. Springboot 配置文件修改

``` yaml
### spring config ###
spring:
  # set profiles avtive
  profiles:
    active: @spring.profiles.active@
```

##### 4. 执行打包操作

``` bash
mvn clean package -dev
```
> IDEA 配置

![](./images/WX20190919-161241@2x.png ':size=600')


## Maven 引入本地项目jar包

``` xml
# 引入本地jar
<dependency>
    <groupId>com.tom.item</groupId>
    <artifactId>item</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${basedir}/src/main/resources/lib/item.jar</systemPath>
</dependency>

# maven 插件设置，比较重要
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <includeSystemScope>true</includeSystemScope>
    </configuration>
</plugin>
```
