# 工具类

> 日常封装用到的工具类

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
