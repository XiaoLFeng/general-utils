package com.xlf.utility.config.app;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

/**
 * 邮件配置类
 * <p>
 * 该类用于配置邮件发送相关配置;
 * 该类应当被继承后使用 {@link org.springframework.context.annotation.Configuration} 注解标记；使用方法如下：
 * <pre>
 * {@code
 *     @Configuration
 *     public class MailConfig extends MailConfigAbstract {
 *          public MailConfig(Environment env) {
 *              super(env);
 *          }
 *     }
 * }
 * </pre>
 *
 * @author xiao_lfeng
 * @version v1.0.9-beta.1.0
 * @since v1.0.9-beta.1.0
 */
@SuppressWarnings("unused")
public abstract class MailConfigAbstract {
    public MailConfigAbstract(Environment env) {
        this.env = env;
    }
    private final Environment env;

    /**
     * 邮件发送配置
     * <p>
     * 用于处理邮件的
     *
     * @return JavaMailSender 邮件发送对象
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mail = new JavaMailSenderImpl();

        // 默认编码
        if (env.getProperty("xutil.mail.default-encoding") != null
                && !Objects.requireNonNull(env.getProperty("xutil.mail.default-encoding")).isEmpty()
        ) {
            mail.setDefaultEncoding(env.getProperty("xutil.mail.default-encoding"));
        } else {
            mail.setDefaultEncoding("UTF-8");
        }
        // 邮件服务器地址
        if (env.getProperty("xutil.mail.host") == null
                || Objects.requireNonNull(env.getProperty("xutil.mail.host")).isEmpty()
        ) {
            throw new IllegalArgumentException("邮件服务器地址不能为空");
        } else {
            mail.setHost(env.getProperty("xutil.mail.host"));
        }
        // 邮件服务器端口
        if (env.getProperty("xutil.mail.port") != null
                && !Objects.requireNonNull(env.getProperty("xutil.mail.port")).isEmpty()
        ) {
            mail.setPort(Integer.parseInt(Objects.requireNonNull(env.getProperty("xutil.mail.port"))));
        } else {
            mail.setPort(25);
        }
        // 邮件服务器用户名
        if (env.getProperty("xutil.mail.username") == null
                || Objects.requireNonNull(env.getProperty("xutil.mail.username")).isEmpty()
        ) {
            throw new IllegalArgumentException("邮件服务器用户名不能为空");
        } else {
            mail.setUsername(env.getProperty("xutil.mail.username"));
        }
        // 邮件服务器密码
        if (env.getProperty("xutil.mail.password") == null
                || Objects.requireNonNull(env.getProperty("xutil.mail.password")).isEmpty()
        ) {
            throw new IllegalArgumentException("邮件服务器密码不能为空");
        } else {
            mail.setPassword(env.getProperty("xutil.mail.password"));
        }
        // 邮件服务器配置
        mail.setJavaMailProperties(new Properties() {{
            put("mail.smtp.auth", true);
            put("mail.smtp.starttls.enable", true);
            put("mail.debug", false);
            put("mail.transport.protocol", "smtp");
        }});

        return mail;
    }
}
