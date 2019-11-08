# SpringBoot

> Springboot 日常笔记

![](./images/spring.png ':size=400') <span style="position: relative; top: -34px; font-size:65px; font-weight: 600; color: #55b432;">Boot</span>


- [springboot-读取系统配置文件](/framework/springboot/springboot?id=springboot-读取系统配置文件)
- [springboot-自定义日志](/framework/springboot/springboot?id=springboot-自定义日志)
- [springboot-自定义异常拦截](/framework/springboot/springboot?id=springboot-自定义异常拦截)
- [springboot-多线程定时任务](/framework/springboot/springboot?id=springboot-多线程定时任务)
- [springboot-redis配置](/framework/springboot/springboot?id=springboot-redis配置)
- [springboot-rabbitmq配置](/framework/springboot/springboot?id=springboot-rabbitmq配置)
- [springboot-日志追踪](/framework/springboot/springboot?id=springboot-日志追踪)

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
     * 自定义参数异常拦截
     */
    @ExceptionHandler(value = { MethodArgumentNotValidException.class, BindException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultPoJo validException(HttpServletRequest request, Exception ex) {
        List<ObjectError> errors = Lists.newArrayList();
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException c = (MethodArgumentNotValidException) ex;
            errors =c.getBindingResult().getAllErrors();
        }
        if (ex instanceof BindException) {
            BindException c = (BindException) ex;
            errors =c.getBindingResult().getAllErrors();
        }
        StringJoiner stringJoiner = new StringJoiner(" , ", "{ ", " }");
        errors.forEach(error -> {
            List<String> rs = Stream.of(error.toString().split(";"))
                    .filter(mg -> mg.contains("default message"))
                    .map(mg -> Stream.of(mg.split(",")).filter(s -> s.contains("default message")).findFirst().orElse(""))
                    .collect(Collectors.toList());
            List<String> list = CommonUtil.splitStr4Temp(String.join("",rs), "default message [{}]");
            stringJoiner.add("[" + list.get(0) + "]" + list.get(1));
        });
        return ResultPoJo.create().code(HttpStatus.BAD_REQUEST.value() + "").msg("参数异常："+ stringJoiner.toString());
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
            msg = "缺少参数!";
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (Objects.equals(HttpRequestMethodNotSupportedException.class, exceptionClazz)) {
            httpStatus = HttpStatus.BAD_REQUEST;
            msg = exception.getMessage();
        } else if ("AccessDeniedException: 不允许访问".equals(ExceptionUtil.getMessage(exception))) {
            msg = "不允许访问!";
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else {
            msg = "服务错误，请稍后重试！（" + exception.getMessage() + "）";
        }

        return new ResponseEntity(
                ResultPoJo.create().code("" + httpStatus.value()).msg(msg),
                httpStatus);
    }

    /**
     * 加载错误信息
     * @return
     */
    public static String loadErrorMsg(String num, Object... msg) {
        //获取错误码配置信息 , hutool 工具读配置文件
        Setting errorSetting = new Setting("error.setting");
        //获取错误码
        String errMsg = errorSetting.get(StrUtil.format("code_{}", num));
        if (ObjectUtil.isNotNull(msg)) {
            errMsg = StrUtil.format(errMsg, msg);
        }
        return errMsg;
    }

}
```

- 异常信息统一管理

``` Setting
# -------------------------------------------------------------
# ----- Setting File with UTF8 -----
# ----- 错误信息code码 -----
# -------------------------------------------------------------

# 错误代码
#code_999为自定义异常
code_999={}
code_998=登录超时
code_997=系统异常请稍后重试

code_401=非授权访问，无效的token
code_402=token 已过期
code_403=权限不足，访问失败
```

- 调用时抛出异常，默认会自动拦截

``` java
throw new MyBaselogicException("999", "系统异常");
```

## SpringBoot 参数验证框架
> 在spring-boot-starter-web包里面有hibernate-validator包，它提供了一系列验证各种参数的方法

- javaBean

``` java
@Data
public class Validate {

    // 空和非空检查: @Null、@NotNull、@NotBlank、@NotEmpty

    @Null(message = "验证是否为 null")
    private Integer isNull;

    @NotNull(message = "验证是否不为 null, 但无法查检长度为0的空字符串")
    private Integer id;

    @NotBlank(message = "检查字符串是不是为 null，以及去除空格后长度是否大于0")
    private String name;

    @NotEmpty(message = "检查是否为 NULL 或者是 EMPTY")
    private List<String> stringList;

    // Boolean值检查: @AssertTrue、@AssertFalse

    @AssertTrue(message = " 验证 Boolean参数是否为 true")
    private Boolean isTrue;

    @AssertFalse(message = "验证 Boolean 参数是否为 false ")
    private Boolean isFalse;

    // 长度检查: @Size、@Length

    @Size(min = 1, max = 2, message = "验证（Array,Collection,Map,String）长度是否在给定范围内")
    private List<Integer> integerList;

    @Length(min = 8, max = 30, message = "验证字符串长度是否在给定范围内")
    private String address;

    // 日期检查: @Future、@FutureOrPresent、@Past、@PastOrPresent

    @Future(message = "验证日期是否在当前时间之后")
    private Date futureDate;

    @FutureOrPresent(message = "验证日期是否为当前时间或之后")
    private Date futureOrPresentDate;

    @Past(message = "验证日期是否在当前时间之前")
    private Date pastDate;

    @PastOrPresent(message = "验证日期是否为当前时间或之前")
    private Date pastOrPresentDate;

    // 其它检查: @Email、@CreditCardNumber、@URL、@Pattern、@ScriptAssert、@UniqueElements

    @Email(message = "校验是否为正确的邮箱格式")
    private String email;

    @CreditCardNumber(message = "校验是否为正确的信用卡号")
    private String creditCardNumber;

    @URL(protocol = "http", host = "127.0.0.1", port = 8080, message = "校验是否为正确的URL地址")
    private String url;

    @Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "正则校验是否为正确的手机号")
    private String phone;

    // 对关联对象元素进行递归校验检查

    @Valid
    @UniqueElements(message = "校验集合中的元素是否唯一")
    private List<CalendarEvent> calendarEvent;

    @Data
    @ScriptAssert(lang = "javascript", script = "_this.startDate.before(_this.endDate)",
            message = "通过脚本表达式校验参数")
    private class CalendarEvent {

        private Date startDate;

        private Date endDate;

    }

    // 数值检查: @Min、@Max、@Range、@DecimalMin、@DecimalMax、@Digits

    @Min(value = 0, message = "验证数值是否大于等于指定值")
    @Max(value = 100, message = "验证数值是否小于等于指定值")
    @Range(min = 0, max = 100, message = "验证数值是否在指定值区间范围内")
    private Integer score;

    @DecimalMin(value = "10.01", inclusive = false, message = "验证数值是否大于等于指定值")
    @DecimalMax(value = "199.99", message = "验证数值是否小于等于指定值")
    @Digits(integer = 3, fraction = 2, message = "限制整数位最多为3，小数位最多为2")
    private BigDecimal money;

}
```

- controller 开启验证（增加 @Valid 参数注解）

```  java
/**
 * Valid注解标明要对参数对象进行数据校验
 */
@PutMapping
@PostMapping
public Map<String, Object> test01(@RequestBody @Valid Validate validate) {
    Map<String, Object> map = new HashMap<>(4);
    ....
    return map;
}
```

- 附件

|  组件名称   | 组件分类  |
|  :---- | :----  |
|@Null |被注释的元素必须为 null |
|@NotNull|被注释的元素必须不为 null|
|@AssertTrue| 	被注释的元素必须为 true|
|@AssertFalse| 	被注释的元素必须为 false|
|@Min(value)| 	被注释的元素必须是一个数字，其值必须大于等于指定的最小值|
|@Max(value)|	被注释的元素必须是一个数字，其值必须小于等于指定的最大值|
|@DecimalMin(value)| 	被注释的元素必须是一个数字，其值必须大于等于指定的最小值|
|@DecimalMax(value)|  	被注释的元素必须是一个数字，其值必须小于等于指定的最大值|
|@Size(max, min)| 	被注释的元素的大小必须在指定的范围内|
|@Digits (integer, fraction)|	 被注释的元素必须是一个数字，其值必须在可接受的范围内|
|@Past|被注释的元素必须是一个过去的日期|
|@Future| 	被注释的元素必须是一个将来的日期|
|@Pattern(value)|	被注释的元素必须符合指定的正则表达式|


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

## SpringBoot 日志追踪

> 此处springboot日志追踪是借助于springcloud的一个组件Sleuth，利用组件在日志里打印TraceId，并根据TraceId追踪到日志记录。

- 添加依赖

``` xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
    <version>2.1.2.RELEASE</version>
</dependency>
```

- 增加logback日志
> 详情请参考 [springboot-自定义日志](/framework/springboot/springboot?id=springboot-自定义日志)

``` xml
  [%X{X-B3-TraceId:-}]
```

- java 代码里获取TraceId

``` java
   @Autowired
   private Tracer tracer;

   //调用
   String tracerId = tracer.currentSpan().context().toString();
```

!> 具体如果使用日志追踪还需要根据业务来，我这里有一套追踪机制是：<br>
  系统出异常捕获，然后发送邮件到指定邮箱并带上`tracerId`，之后系统维护人员可根据`tracerId`定位到日志请求过程中所有的日志情况，然后处理。

## SpringBoot 集成WebSocket

> 摘自[SpringBoot2.0集成WebSocket，实现后台向前端推送信息](https://blog.csdn.net/moshowgame/article/details/80275084)

##### 什么是WebSocket?

![](./images/webscoket01.png ':size=400px')

WebSocket协议是基于TCP的一种新的网络协议。它实现了浏览器与服务器全双工(full-duplex)通信——允许服务器主动发送信息给客户端。

##### 为什么需要 WebSocket？

初次接触 WebSocket 的人，都会问同样的问题：我们已经有了 HTTP 协议，为什么还需要另一个协议？它能带来什么好处？

- 答案很简单，因为 HTTP 协议有一个缺陷：通信只能由客户端发起，HTTP 协议做不到服务器主动向客户端推送信息。

![](./images/webscoket02.png ':size=500px')

举例来说，我们想要查询当前的排队情况，只能是页面轮询向服务器发出请求，服务器返回查询结果。轮询的效率低，非常浪费资源（因为必须不停连接，或者 HTTP 连接始终打开）。因此WebSocket 就是这样发明的。

话不多说，马上进入干货时刻。

##### maven依赖

SpringBoot2.0对WebSocket的支持简直太棒了，直接就有包可以引入

``` xml
<dependency>  
   <groupId>org.springframework.boot</groupId>  
   <artifactId>spring-boot-starter-websocket</artifactId>  
</dependency>
```

##### WebSocketConfig

启用WebSocket的支持也是很简单，几句代码搞定

``` java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 开启WebSocket支持
 * @author zhengkai
 */
@Configuration  
public class WebSocketConfig {  

    @Bean  
    public ServerEndpointExporter serverEndpointExporter() {  
        return new ServerEndpointExporter();  
    }  

}
```

##### WebSocketServer

因为WebSocket是类似客户端服务端的形式(采用ws协议)，那么这里的WebSocketServer其实就相当于一个ws协议的Controller直接@ServerEndpoint("/websocket")@Component启用即可，然后在里面实现@OnOpen,@onClose,@onMessage等方法

``` java
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.extern.slf4j.Slf4j;


@ServerEndpoint("/websocket/{sid}")
@Component
public class WebSocketServer {

	static Log log=LogFactory.get(WebSocketServer.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String sid="";
    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        log.info("有新窗口开始监听:"+sid+",当前在线人数为" + getOnlineCount());
        this.sid=sid;
        try {
        	 sendMessage("连接成功");
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
    	log.info("收到来自窗口"+sid+"的信息:"+message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	/**
	 *
	 * @param session
	 * @param error
	 */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
	/**
	 * 实现服务器主动推送
	 */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message,@PathParam("sid") String sid) throws IOException {
    	log.info("推送消息到窗口"+sid+"，推送内容:"+message);
        for (WebSocketServer item : webSocketSet) {
            try {
            	//这里可以设定只推送给这个sid的，为null则全部推送
            	if(sid==null) {
            		item.sendMessage(message);
            	}else if(item.sid.equals(sid)){
            		item.sendMessage(message);
            	}
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
```
##### 消息推送

至于推送新信息，可以再自己的Controller写个方法调用WebSocketServer.sendInfo();即可

``` java
@Controller
@RequestMapping("/checkcenter")
public class CheckCenterController {

	//页面请求
	@GetMapping("/socket/{cid}")
	public ModelAndView socket(@PathVariable String cid) {
		ModelAndView mav=new ModelAndView("/socket");
		mav.addObject("cid", cid);
		return mav;
	}
	//推送数据接口
	@ResponseBody
	@RequestMapping("/socket/push/{cid}")
	public ApiReturnObject pushToWeb(@PathVariable String cid,String message) {  
		try {
			WebSocketServer.sendInfo(message,cid);
		} catch (IOException e) {
			e.printStackTrace();
			return ApiReturnUtil.error(cid+"#"+e.getMessage());
		}  
		return ApiReturnUtil.success(cid);
	}
}
```

##### 页面发起socket请求

然后在页面用js代码调用socket，当然，太古老的浏览器是不行的，一般新的浏览器或者谷歌浏览器是没问题的。还有一点，记得协议是ws的哦，如果像我这样封装了一些basePath的路径类，可以replace(“http”,“ws”)来替换协议

``` js
<script>
    var socket;  
    if(typeof(WebSocket) == "undefined") {  
        console.log("您的浏览器不支持WebSocket");  
    }else{  
        console.log("您的浏览器支持WebSocket");  
        	//实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接  
            //等同于socket = new WebSocket("ws://localhost:8083/checkcentersys/websocket/20");  
            socket = new WebSocket("${basePath}websocket/${cid}".replace("http","ws"));  
            //打开事件  
            socket.onopen = function() {  
                console.log("Socket 已打开");  
                //socket.send("这是来自客户端的消息" + location.href + new Date());  
            };  
            //获得消息事件  
            socket.onmessage = function(msg) {  
                console.log(msg.data);  
                //发现消息进入    开始处理前端触发逻辑
            };  
            //关闭事件  
            socket.onclose = function() {  
                console.log("Socket已关闭");  
            };  
            //发生了错误事件  
            socket.onerror = function() {  
                alert("Socket发生了错误");  
                //此时可以尝试刷新页面
            }  
            //离开页面时，关闭socket
            //jquery1.8中已经被废弃，3.0中已经移除
            // $(window).unload(function(){  
            //     socket.close();  
            //});  
    }
</script>
```

##### 运行效果

v1.1的效果，刚刚修复了日志，并且支持指定监听某个端口，代码已经全部更新，现在是这样的效果

- 打开两个页面：<br>
  [http://localhost:8083/checkcentersys/checkcenter/socket/20](http://localhost:8083/checkcentersys/checkcenter/socket/20)
  [http://localhost:8083/checkcentersys/checkcenter/socket/22](http://localhost:8083/checkcentersys/checkcenter/socket/22)
- 向前端推送数据：<br>
  [http://localhost:8083/checkcentersys/checkcenter/socket/push/20?message=cccccccccc](http://localhost:8083/checkcentersys/checkcenter/socket/push/20?message=cccccccccc)
  [http://localhost:8083/checkcentersys/checkcenter/socket/push/22?message=xxxxx123xxxx](http://localhost:8083/checkcentersys/checkcenter/socket/push/22?message=xxxxx123xxxx)

![](./images/webscoket03.jpeg ':size=800px')

![](./images/webscoket04.jpeg ':size=800px')
