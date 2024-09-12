package com.xlf.utility.config;

import com.xlf.utility.UtilProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


/**
 * 邮件配置类
 * <p>
 * 该类用于配置邮件发送相关配置;
 * 该类应当被继承后使用 {@link org.springframework.context.annotation.Configuration} 注解标记；使用方法如下：
 *
 * @author xiao_lfeng
 * @version v1.0.9-beta.1.19
 * @since v1.0.9-beta.1.0
 */
@SuppressWarnings("unused")
public class UtilConfiguration {

    private static final Logger log = LoggerFactory.getLogger(UtilConfiguration.class);
    private final UtilProperties properties;

    public UtilConfiguration(UtilProperties properties) {
        this.properties = properties;
    }

    /**
     * 邮件发送配置
     * <p>
     * 用于处理邮件的
     *
     * @return JavaMailSender 邮件发送对象
     */
    public JavaMailSenderImpl javaMailSender() {
        log.debug(properties.toString());
        JavaMailSenderImpl mail = new JavaMailSenderImpl();

        // 默认编码
        mail.setDefaultEncoding(properties.getMailDefaultEncoding());
        // 邮件服务器地址
        mail.setHost(properties.getMailHost());
        // 邮件服务器端口
        mail.setPort(properties.getMailPort());
        // 邮件服务器用户名
        if (properties.getMailUsername() == null || properties.getMailUsername().isEmpty()) {
            throw new IllegalArgumentException("邮件服务器用户名不能为空");
        } else {
            mail.setUsername(properties.getMailUsername());
        }
        // 邮件服务器密码
        if (properties.getMailPassword() == null || properties.getMailPassword().isEmpty()) {
            throw new IllegalArgumentException("邮件服务器密码不能为空");
        } else {
            mail.setPassword(properties.getMailPassword());
        }

        mail.setJavaMailProperties(new Properties() {{
            setProperty("mail.smtp.auth", "true");
            setProperty("mail.smtp.starttls.enable", "true");
            setProperty("mail.debug", "false");
            setProperty("mail.transport.protocol", "smtp");
        }});

        return mail;
    }
}
