# 工具类

> 日常封装用到的工具类

- [springboot-邮件发送](/framework/util/util?id=springboot-邮件发送)
- [邮件延时发送](/framework/util/util?id=邮件延时发送)
- [spring容器中获取静态对象](/framework/util/util?id=spring容器中获取静态对象)
- [获取setting-公共配置文件](/framework/util/util?id=获取setting-公共配置文件)
- [commonutil-公共通用方法](/framework/util/util?id=commonutil-公共通用方法)

## SpringBoot 邮件发送

##### 1. 添加maven

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

##### 2. 增加springboot配置文件

``` yaml
### spring config ###
spring:
  # email config
  mail:
    host: smtp.gmail.com
    port: 465
    username: *****@gmail.com
    password: *****@
    protocol: smtp
    properties.mail.smtp.auth: true
    properties.mail.smtp.port: 465
    properties.mail.display.sendmail: Javen
    properties.mail.display.sendname: Spring Boot Guide Email
    properties.mail.smtp.starttls.enable: true
    properties.mail.smtp.starttls.required: true
    properties.mail.smtp.ssl.enable: true
    default-encoding: utf-8
    from: *****@@gmail.com
    # 发送到指定邮箱
    to:
      - *****@@gmail.com
```

##### 3. util工具类

``` java

import com.spring.graspspring.entity.EmailConfig;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 邮件发送工具类
 *
 * @author zhangby
 * @date 5/9/19 11:01 am
 */
public class EmailUtil {

    /**
     * mail sender
     */
    private static JavaMailSender mailSender = SpringContextUtil.getBean(JavaMailSender.class);

    /**
     * mail config
     */
    private static EmailConfig emailConfig = SpringContextUtil.getBean(EmailConfig.class);

    /**
     * 发送邮件
     */
    public static void send(String title, String content) {
        try {
            emailConfig.getTo().forEach(toEmail -> {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(emailConfig.getFrom());
                message.setTo(toEmail);
                message.setSubject(title);
                message.setText(content);
                mailSender.send(message);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 邮件延时发送

!> 当项目中加入系统异常邮件推送机制时，可能会遇到同一时刻某个特定的异常，导致邮件大量的集中推送，为避免邮箱被大量垃圾邮件堆积。此处加入邮件延时发送配置，可根据用户配置的邮件发送,间隔时间才可正常推送。具体方式，修改如上[邮件推送](/framework/util/util?id=springboot-邮件发送)代码。加入邮件延时推送方法

##### 增加系统延时推送配置

``` yaml
spring:
  mail:
    # 延时发送单位默认分钟
    delaySend: 10
```

##### 代码修改

``` java
###### 配置文件读取配置修改 ########
/**
 * 邮件配置
 *
 * @author zhangby
 * @date 5/9/19 2:35 pm
 */
@Component
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class EmailConfig {
    private Integer delaySend;
    private String from;
    private List<String> to;
}

###### 邮件发送修改 ########

 /**
  * 设置延迟推送
  */
private static Map<String, Date> delaySendMap = Maps.newConcurrentMap();

/**
 * 延时发送，避免同一个异常集中发送邮件，造成大量垃圾邮件
 * @param key 阻塞邮件标识
 */
public static void delaySend(String title, String content,String key) {
    //获取指定标识，最新邮件发送时间
    Date date = Optional.ofNullable(delaySendMap)
            .map(delayMap -> delayMap.get(key))
            .orElse(null);
    //第一次发送
    if (date == null) {
        //支持发送
        send(title,content);
        //更新时间
        delaySendMap.put(key, new Date());
    } else {
        //验证是否到达指定发送时间
        Date delayTime = DateUtil.offsetMinute(date, emailConfig.getDelaySend());
        if (delayTime.before(new Date())) {
            //支持发送
            send(title,content);
            delaySendMap.put(key, new Date());
        }
    }
}
```

##### 测试邮件发送

``` java
@RunWith(SpringRunner.class)
@SpringBootTest
public class GraspSpringApplicationTests {

    @Test
    public void contextLoads() {
        for (int i : NumberUtil.range(1, 10)) {
            EmailUtil.delaySend("测试延时推送","测试延时推送","测试");
        }
    }

}
```

?> 此时配置的时间间隔是10分钟，在10分钟内发的所有邮件只有一封可以送达。

## Spring容器中获取静态对象

> 静态对象，从spring容器中获取对象

``` java
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Static injection of spring objects
 *
 * @author zhangby
 * @date 2019-05-15 11:45
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 设置 setApplicationContext
     * @param applicationContext application
     * @throws BeansException beansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtil.applicationContext == null) {
            SpringContextUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 获取applicationContext
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     * @param name 名称
     * @return Object
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     * @param clazz class
     * @param <T> T
     * @return T
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * @param name name
     * @param clazz class
     * @param <T> T
     * @return T
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}
```

## 获取setting 公共配置文件

> setting读取借助 Hutool 工具包

- error.setting

``` setting
# -------------------------------------------------------------
# ----- Setting File with UTF8 -----
# -----      错误信息code码     -----
# -------------------------------------------------------------

# 错误代码
#code_999为自定义异常
code_999={}
code_998=login timeout

code_401=unauthorized access, invalid token
code_403=insufficient permissions, access failed
```

- ConfigUtil.java

``` java
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.setting.Setting;

/**
 * 获取公用配置文件
 *
 *
 * @author zhangby
 * @date 2019-05-13 16:13
 */
public class ConfigUtil {
    /**
     * 异常错误码配置
     */
    private static Setting errorSetting = null;

    /**
     * 获取错误码配置
     */
    public static Setting getErrorSetting() {
        return configFunction("error.setting", errorSetting);
    }

    /**
     * 读取系统配置信息
     *
     * @param configName
     * @return
     */
    private static Setting configFunction(String configName, Setting prop) {
        if (ObjectUtil.isNull(prop)) {
            prop = new Setting(configName);
        }
        return prop;
    }

}
```

## CommonUtil 公共通用方法

``` java
/**
 * list数据转换
 *
 * @param list list对象
 * @param func lamdba 表达式 function
 * @param <E>  原对象
 * @param <T>  转换完的对象
 * @return List<E>
 */
public static <E, T> List<E> convers(List<T> list, Function<T, E> func) {
    return list.stream().collect(ArrayList::new, (li, p) -> li.add(func.apply(p)), List::addAll);
}

/**
 * 异常捕获
 * @param resolver resolver
 * @param <T> T
 * @return
 */
public static <T> Optional<T> resolve(Supplier<T> resolver) {
    Optional<T> optional = Optional.empty();
    try {
        T result = resolver.get();
        optional = Optional.ofNullable(result);
    } catch (Exception e) {
        logger.info("系统执行异常：\n" + ExceptionUtil.stacktraceToString(e));
        optional = Optional.empty();
    }
    return optional;
}
```
