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

## SpringBoot Redis配置

> Springboot 内置了Redis模块可以直接使用，这里对redis进行了一些简单的封装，优化了存取json的转换。

- 引入maven配置
``` xml
<!--redis-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

- 增加系统配置文件
``` yaml
spring:
  # redis config
  redis:
    host: *****
    port: 6379
    password:
    database: 1
    timeout: 5000
    jedis:
      pool:
        max-active: 1
        max-wait: -1
        max-idle: 8
        min-idle: 0
```

- 添加Service方法

``` java
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * redis service
 *
 * @author zhangby
 * @date 2019-05-15 09:34
 */
@Service
public class IRedisService {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * redis service
     */
    @Autowired
    protected StringRedisTemplate redisTemplate;

    /**
     * Write to redis cache
     *
     * @param key key
     * @param value value
     * @return boolean
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        if (value != null) {
            try {
                ValueOperations operations = redisTemplate.opsForValue();
                if (value instanceof String) {
                    operations.set(key, value.toString());
                } else {
                    operations.set(key, JSON.toJSONString(value));
                }
                result = true;
            } catch (Exception e) {
                logger.info("Writing redis cache failed! The error message is:" + e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Write redis cache (set expire survival time)
     *
     * @param key key
     * @param value value
     * @param expire time
     * @return boolean
     */
    public boolean set(final String key, Object value, Long expire) {
        boolean result = false;
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            if (value instanceof String) {
                operations.set(key, value.toString());
            } else {
                operations.set(key, JSON.toJSONString(value));
            }
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.info("Writing to the redis cache (setting the expire lifetime) failed! The error message is:" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    /**
     * Read redis cache
     *
     * @param key key
     * @return object
     */
    public Object get(final String key) {
        Object result = null;
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            result = operations.get(key);
        } catch (Exception e) {
            logger.info("Failed to read redis cache! The error message is:" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Read redis to entity
     *
     * @param key   redis key
     * @param clazz 实体类class
     * @param <T>   泛型
     * @return T
     */
    public <T> T getBean(final String key, Class<T> clazz) {
        return Optional.ofNullable(get(key))
                .map(o -> JSON.parseObject(o.toString(), clazz))
                .orElse(null);
    }


    /**
     * Determine if there is a corresponding key in the redis cache
     *
     * @param key key
     * @return boolean
     */
    public boolean exists(final String key) {
        boolean result = false;
        try {
            result = redisTemplate.hasKey(key);
        } catch (Exception e) {
           logger.info("Determine if there is a corresponding key in the redis cache failed! The error message is:" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Redis deletes the corresponding value according to the key
     *
     * @param key key
     * @return boolean
     */
    public boolean remove(final String key) {
        boolean result = false;
        try {
            if (exists(key)) {
                redisTemplate.delete(key);
            }
            result = true;
        } catch (Exception e) {
            logger.info("Redis fails to delete the corresponding value according to the key! The error message is:" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Redis deletes the corresponding value according to the keywords batch
     *
     * @param keys keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }
}
```

## SpringBoot RabbitMQ配置
> 增加rabbitMQ配置

- 添加Maven配置
``` xml
<!--rabbitmq-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

- yml 系统配置
``` yaml
  spring:
  # rabbitMq config
    rabbitmq:
      host: *****
      port: 5672
      username: guest
      password: guest
      publisher-confirms: true # Whether to enable publisher confirms.
      virtual-host: / # Virtual host to use when connecting to the broker.
      listener:
        simple:
          concurrency: 1 # Minimum number of listener invoker threads.
          max-concurrency: 1 # Maximum number of listener invoker threads.
          prefetch: 1 # Maximum number of unacknowledged messages that can be outstanding at each consumer.
```

- rabbitMQ系统配置

``` java
import com.way.pacific.common.constants.Constants;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * rabbit config
 *
 * @author zhangby
 * @date 2019-05-16 09:51
 */
@Configuration
public class RabbitMqConfig {

    /**
     * Create a log message queue
     *
     * @return
     */
    @Bean
    public Queue loggerQueue() {
        /** Create permanent queue data and still save after reboot */
        return new Queue(Constants.QUEUE_SYS_LOG, true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

}
```

- 添加rabbitMQ监听

``` java
import java.io.IOException;

/**
 * mq for Log
 *
 * @author zhangby
 * @date 2019-05-16 09:48
 */
@Component
public class LogReceiver {

    /**
     * receiver rabbitMq msg
     *
     * @param data    data
     * @param channel channel
     * @param message message
     * @throws IOException ioException
     */
    @RabbitHandler
    @Transactional(rollbackFor = Exception.class)
    @RabbitListener(queues = "queue.sys.log", containerFactory = "rabbitListenerContainerFactory")
    public void receiver(@Payload String data, Channel channel, Message message) throws IOException {
        try {
            Log logException = JSON.parseObject(data, Log.class);
            logException.insert();
        } catch (Exception e) {
            //The MQ has been accepted and consumed, and will not be repeated for consumption.
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            //Manual rollback
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
        }
    }

}
```

- java 调用rabbitMQ

``` java
    //引入
    @Autowired
    RabbitTemplate rabbitTemplate;

    //调用
    Log log = new Log()
            .setId(IdUtil.simpleUUID())
            .setType("1")
            .setTitle("系统异常日志信息")
            .setCreateBy(UserUtil.getCurrentUser().getId())
            .setCreateDate(new Date())
            .setRemoteAddr(ServletUtil.getClientIP(request))
            .setRequestUri(CommonUtil.notEmpty(request, r -> r.getRequestURL().toString()))
            .setMethod(CommonUtil.notEmpty(request, r -> r.getMethod()))
            .setParams(JSON.toJSONString(ServletUtil.getParams(request)))
            .setException(CommonUtil.getExceptionMsg(exception));
    //send MQ
    rabbitTemplate.convertAndSend(
            Constants.QUEUE_SYS_LOG,
            JSON.toJSONString(log)
```
