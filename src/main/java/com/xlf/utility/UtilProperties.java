package com.xlf.utility;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.stereotype.Component;

/**
 * 邮件配置属性
 * <p>
 * 用于配置邮件发送的相关属性
 *
 * @author xiao_lfeng
 * @version 1.0.9-beta.1.0
 * @since 1.0.9-beta.1.0
 */
@Component
@SuppressWarnings("unused")
public class UtilProperties {
    /**
     * 默认编码
     */
    private String mailDefaultEncoding = "UTF-8";
    /**
     * 邮件服务器地址
     */
    private String mailHost = "smtp.qiye.aliyun.com";
    /**
     * 邮件服务器用户名
     */
    private String mailUsername = "";
    /**
     * 邮件服务器密码
     */
    private String mailPassword = "";
    /**
     * 邮件服务器昵称
     */
    private String mailNickname = "";
    /**
     * 邮件服务器端口
     */
    private Integer mailPort = 25;

    /**
     * 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

    /**
     * 是否开启事务
     */
    private boolean dbTransaction = false;

    /**
     * 获取默认编码
     *
     * @return 默认编码
     */
    public String getMailDefaultEncoding() {
        return mailDefaultEncoding;
    }

    /**
     * 设置默认编码
     *
     * @param mailDefaultEncoding 默认编码
     */
    public void setMailDefaultEncoding(String mailDefaultEncoding) {
        this.mailDefaultEncoding = mailDefaultEncoding;
    }

    /**
     * 获取邮件服务器地址
     *
     * @return 邮件服务器地址
     */
    public String getMailHost() {
        return mailHost;
    }

    /**
     * 设置邮件服务器地址
     *
     * @param mailHost 邮件服务器地址
     */
    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    /**
     * 获取邮件服务器用户名
     *
     * @return 邮件服务器用户名
     */
    public String getMailUsername() {
        return mailUsername;
    }

    /**
     * 设置邮件服务器用户名
     *
     * @param mailUsername 邮件服务器用户名
     */
    public void setMailUsername(String mailUsername) {
        this.mailUsername = mailUsername;
    }

    /**
     * 获取邮件服务器密码
     *
     * @return 邮件服务器密码
     */
    public String getMailPassword() {
        return mailPassword;
    }

    /**
     * 设置邮件服务器密码
     *
     * @param mailPassword 邮件服务器密码
     */
    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    /**
     * 获取邮件服务器昵称
     *
     * @return 邮件服务器昵称
     */
    public String getMailNickname() {
        return mailNickname;
    }

    /**
     * 设置邮件服务器昵称
     *
     * @param mailNickname 邮件服务器昵称
     */
    public void setMailNickname(String mailNickname) {
        this.mailNickname = mailNickname;
    }

    /**
     * 获取邮件服务器端口
     *
     * @return 邮件服务器端口
     */
    public Integer getMailPort() {
        return mailPort;
    }

    /**
     * 设置邮件服务器端口
     *
     * @param mailPort 邮件服务器端口
     */
    public void setMailPort(Integer mailPort) {
        this.mailPort = mailPort;
    }

    /**
     * 获取数据库类型
     *
     * @return 数据库类型
     */
    public DbType getDbType() {
        return dbType;
    }

    /**
     * 设置数据库类型
     *
     * @param dbType 数据库类型
     */
    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    /**
     * 是否开启事务
     *
     * @return 是否开启事务
     */
    public boolean isDbTransaction() {
        return dbTransaction;
    }

    /**
     * 设置是否开启事务
     *
     * @param dbTransaction 是否开启事务
     */
    public void setDbTransaction(boolean dbTransaction) {
        this.dbTransaction = dbTransaction;
    }

    @Override
    public String toString() {
        return "UtilProperties{" +
                "mailDefaultEncoding='" + mailDefaultEncoding + '\'' +
                ", mailHost='" + mailHost + '\'' +
                ", mailUsername='" + mailUsername + '\'' +
                ", mailPassword='" + mailPassword + '\'' +
                ", mailNickname='" + mailNickname + '\'' +
                ", mailPort=" + mailPort +
                ", dbType=" + dbType +
                ", dbTransaction=" + dbTransaction +
                '}';
    }
}
