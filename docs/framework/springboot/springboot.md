# SpringBoot

> Springboot 日常笔记

## Springboot 读取系统配置文件

> springboot 配置文件，默认支持properties，yml 所以两者通用

- application.yml

``` yaml
# 自定义配置信息
grasp:
  # 中心服务
  ws: http://********
  # 需要推送的服务IP
  server:
    - ********
    - ********
  # url
  lotteryUrl:
      magNum: https://********
      daMaCai: https:/********
```

- GraspConfig.java

``` java
import cn.hutool.core.lang.Dict;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 服务配置信息 entity
 *
 * @author zhangby
 * @date 2/9/19 1:58 pm
 */
@Component
@ConfigurationProperties(prefix = "grasp")
@Data
public class GraspConfig {
    private String ws;
    private List<String> server;
    private Dict lotteryUrl;
}
```

?> @Data 注解：用的lombok，默认生成 set get方法


- 调用时

``` java
/**
 * 静态加载配置信息
 */
public static GraspConfig graspConfig = SpringContextUtil.getBean(GraspConfig.class);

/**
 * 动态加载
 */
@Autowired
private GraspConfig graspConfig;


/**
* 动态加载直接获取配置文件
*/
@Value("${grasp.lotteryUrl.magNum}")
private String url;

```

## SpringBoot 自定义日志
> SpringBoot之自定义日志 -- logback，springboot默认支持logback。<br>只需要在resources下加入 logback-spring.xml。

- logback-spring.xml

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- 彩色日志 -->
    <!-- 彩色日志依赖的渲染类 -->
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
    <conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
    <conversionRule conversionWord="wEx"
                    converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />
    <!-- 彩色日志格式 -->
    <property name="CONSOLE_LOG_PATTERN"
              value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}" />

    <!-- Console 输出设置 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>utf8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>debug</level>
            <onMatch>DENY</onMatch>
            <onMismatch>NEUTRAL</onMismatch>
        </filter>
    </appender>

    <!--本地日志输出 INFO-->
    <appender name="INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <FileNamePattern>../logs/grasp-spring/log-%d{yyyy-MM-dd}.%i.log</FileNamePattern>
            <MaxHistory>10</MaxHistory>
            <maxFileSize>20MB</maxFileSize>
            <totalSizeCap>2GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>[%-5level] %d{HH:mm:ss.SSS} [%thread] [%X{X-B3-TraceId:-}] %logger{36} - %msg%n</pattern>
            <charset>utf8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 时间滚动输出 level为 grasp 日志 -->
    <appender name="file—graspLottory" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <FileNamePattern>../logs/grasp-spring/grasp-logs-%d{yyyy-MM-dd}.%i.log</FileNamePattern>
            <MaxHistory>10</MaxHistory>
            <maxFileSize>20MB</maxFileSize>
            <totalSizeCap>2GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>[%-5level] %d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{X-B3-TraceId:-}]  %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <!--自定义日志文件-->
    <logger name="GRASP_LOGS" additivity="false">
        <appender-ref ref="file—graspLottory"/>
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="INFO"/>
    </logger>
    <!-- 控制台输出日志级别 -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="INFO"/>
    </root>
</configuration>
```

- java调用时

``` java
private final Logger logger = LoggerFactory.getLogger(Constants.GRASP_LOGS);
```

## SpringBoot 自定义异常拦截

> 系统中通常会有自定义异常拦截的功能，此处介绍SpringBoot拦截机制

- 自定义异常类

``` java
/**
 * 自定义异常
 *
 * @author zhangby
 * @date 2017/11/30 下午7:10
 */
public class MyBaselogicException extends RuntimeException {
    private static final long serialVersionUID = -6317037305924958356L;
    /**
     * 错误代码
     */
    private String num;
    /**
     * msg参数
     */
    private Object[] msg;

    public MyBaselogicException(String num) {
        this.num = num;
    }

    public MyBaselogicException(String num, Object... msg) {
        this.num = num;
        this.msg = msg;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Object[] getMsg() {
        return msg;
    }

    public void setMsg(Object[] msg) {
        this.msg = msg;
    }
}
```

- 增加异常拦截机制

``` java

import com.alibaba.fastjson.JSON;
import com.spring.graspspring.entity.ResultPoJo;
import com.spring.graspspring.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;


/**
 * 自定义异常拦截
 *
 * @author zhangby
 * @date 2018/11/5 4:19 PM
 */
@ControllerAdvice
@ResponseBody
public class MyBaseExceptionHandler {

    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 捕捉到的异常
     *
     * @param exception exception
     * @return ResponseEntity<ResultPoJo>
     */
    @ExceptionHandler(value = MyBaselogicException.class)
    public ResponseEntity<ResultPoJo> handleServiceException(MyBaselogicException exception) {
        logger.info("ERROR : " + JSON.toJSONString(CommonUtil.loadException2ResultPoJo(exception)));
        logger.info("");
        return new ResponseEntity(
                CommonUtil.loadException2ResultPoJo(exception),
                HttpStatus.OK);
    }

    /**
     * 捕获其他异常
     *
     * @param exception exception
     * @return ResponseEntity
     */
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResultPoJo> hadleServerException(Exception exception) {
        exception.printStackTrace();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = "服务错误，请稍后重试！";
        Class exceptionClazz = exception.getClass();
        if (Objects.equals(MissingServletRequestParameterException.class, exceptionClazz)) {
            msg = "Missing parameters!";
            httpStatus = HttpStatus.OK;
        } else if (Objects.equals(HttpRequestMethodNotSupportedException.class, exceptionClazz)) {
            httpStatus = HttpStatus.OK;
            msg = exception.getMessage();
        } else if ("org.springframework.security.access.AccessDeniedException: 不允许访问".equals(exception.toString())) {
            httpStatus = HttpStatus.OK;
            msg = "insufficient permissions, access failed";
        } else {
            httpStatus = HttpStatus.OK;
            msg = "Service error, please try again later!";
        }
        return new ResponseEntity(
                ResultPoJo.create().code("999").msg(msg),
                httpStatus);
    }

}
```

- 调用时抛出异常，默认会自动拦截

``` java
throw new MyBaselogicException("999", "系统异常");
```

## SpringBoot 多线程定时任务
> SpringBoot 内置了定时任务的功能，集成了Corn可以简单方便的去调用。<br> 定时任务 [corn表达式](http://cron.qqe2.com/)

- 增加多线程配置

``` java
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 定时任务，开启异步事件的支持
 *
 * @author zhangby
 * @date 3/9/19 10:16 am
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    private int corePoolSize = 15;
    private int maxPoolSize = 200;
    private int queueCapacity = 15;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }

}
```

- 开启定时任务

``` java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *
 * @author zhangby
 * @date 3/9/19 10:23 am
 */
@Component
@EnableScheduling
public class GraspCorn {
    /**
     * 每秒运行一次
     */
    @Async
    @Scheduled(fixedDelay = 1000,initialDelay = 5000)
    public void scanScheduleTask() {

    }

    /**
     * 每天4点执行一次
     */
    @Async
    @Scheduled(cron = "0 0 4 * * ?")
    public void scheduleTask(){

    }
}
```
