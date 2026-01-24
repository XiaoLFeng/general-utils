package com.xlf.utility.constant;

import org.jetbrains.annotations.Contract;

/**
 * HTTP 请求头常量类
 * <p>
 * 提供系统中常用的 HTTP 请求头名称常量，方便开发人员快速引用，
 * 避免直接硬编码请求头字段，提高代码的一致性与可维护性。
 * 此类的存在可以让 HTTP 请求处理逻辑更加直观，同时减少可能的拼写错误。
 * <p>
 * 此类中的所有字段均为 {@code public static final} 类型，即全局可用的常量。
 *
 * <p>
 * 注意：
 * <ul>
 * <li>此类为工具类，不应该被实例化，请不要尝试通过 {@code new HttpHeaderConstant()} 创建实例。</li>
 * <li>所有常量均为标准 HTTP 命名规范，请在正确的场景中引用。</li>
 * </ul>
 *
 * <p>
 * 使用此类的建议：
 * <ul>
 * <li>推荐在任何与 HTTP 请求处理相关的逻辑中引用此类常量，避免直接声明重复字符串。</li>
 * <li>如果需要扩展新请求头字段，建议遵循标准的 HTTP 头命名规则，并进行适当的注释。</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @version v1.0.4-SNAPSHOT
 * @since v1.0.4-SNAPSHOT
 */
@SuppressWarnings("unused")
public class HttpHeaderConstant {

    /**
     * Authorization 请求头字段
     * <p>
     * 用于携带身份验证信息的 HTTP 请求头。
     */
    public final static String AUTHORIZATION = "Authorization";
    /**
     * Accept 请求头字段
     * <p>
     * 指定客户端能够接收的内容类型。
     */
    public final static String ACCEPT = "Accept";
    /**
     * Content-Type 请求头字段
     * <p>
     * 指定实体正文的媒体类型。
     */
    public final static String CONTENT_TYPE = "Content-Type";
    /**
     * User-Agent 请求头字段
     * <p>
     * 包含发出请求的用户代理的信息。
     */
    public final static String USER_AGENT = "User-Agent";
    /**
     * Host 请求头字段
     * <p>
     * 指定服务器的域名和端口号。
     */
    public final static String HOST = "Host";
    /**
     * Origin 请求头字段
     * <p>
     * 表示跨域请求的来源。
     */
    public final static String ORIGIN = "Origin";
    /**
     * Referer 请求头字段
     * <p>
     * 表示请求的来源页面的地址。
     */
    public final static String REFERER = "Referer";
    /**
     * X-API-KEY 请求头字段
     * <p>
     * 用于携带 API 密钥的自定义请求头。
     */
    public final static String X_API_KEY = "X-API-KEY";
    /**
     * X-Auth-Type 请求头字段
     * <p>
     * 用于标识认证类型的自定义请求头。
     * 例如，可以用于区分不同的认证方式（如 OAuth、JWT 等）。
     */
    public final static String X_AUTH_TYPE = "X-AUTH-TYPE";
    /**
     * X-Requested-With 请求头字段
     * <p>
     * 通常用于标识AJAX请求。
     */
    public final static String X_REQUESTED_WITH = "X-Requested-With";
    /**
     * X-SSO-USER-ID 请求头字段
     * <p>
     * 用于携带单点登录（SSO）用户ID的自定义请求头。
     */
    public final static String X_SSO_USER_ID = "X-Sso-User-Id";
    /**
     * X-SSO-USERNAME 请求头字段
     * <p>
     * 用于携带单点登录（SSO）用户名的自定义请求头。
     */
    public final static String X_SSO_USERNAME = "X-Sso-Username";
    /**
     * X-SSO-NICKNAME 请求头字段
     * <p>
     * 用于携带单点登录（SSO）用户昵称的自定义请求头。
     */
    public final static String X_SSO_NICKNAME = "X-Sso-Nickname";
    /**
     * X-SSO-AVATAR 请求头字段
     * <p>
     * 用于携带单点登录（SSO）用户头像的自定义请求头。
     */
    public final static String X_SSO_AVATAR = "X-Sso-Avatar";
    /**
     * X-SSO-TOKEN 请求头字段
     * <p>
     * 用于携带单点登录（SSO）令牌的自定义请求头。
     */
    public final static String X_SSO_TOKEN = "X-Sso-Token";
    /**
     * X-SSO-AUTH-CODE 请求头字段
     * <p>
     * 用于携带单点登录（SSO）授权码的自定义请求头。
     */
    public final static String X_SSO_AUTH_CODE = "X-Sso-Authorization-Code";
    /**
     * X-WEAPP-AUTH-CODE 请求头字段
     * <p>
     * 用于携带微信小程序（WEAPP）授权码的自定义请求头。
     * 授权码格式为 "WEAPP:{UUID}"，由微信小程序登录接口生成。
     */
    public final static String X_WEAPP_AUTH_CODE = "X-Weapp-Authorization-Code";
    /**
     * X-SSO-ROLE-ID 请求头字段
     * <p>
     * 用于携带单点登录（SSO）角色ID的自定义请求头。
     */
    public final static String X_SSO_ROLE_ID = "X-Sso-Role-Id";
    /**
     * X-SSO-REFRESH-TOKEN 请求头字段
     * <p>
     * 用于携带单点登录（SSO）刷新令牌的自定义请求头。
     */
    public final static String X_SSO_REFRESH_TOKEN = "X-Sso-Refresh-Token";
    /**
     * X-Wechat-Openid 请求头字段
     * <p>
     * 用于携带微信 OpenID 的自定义请求头。
     */
    public final static String X_WECHAT_OPEN_ID = "X-Wechat-Openid";
    /**
     * X-Wechat-Openid 请求头字段
     * <p>
     * 用于携带微信 OpenID 的自定义请求头。
     */
    public final static String X_WECHAT_UNION_ID = "X-Wechat-Unionid";
    /**
     * X-Office-ID 请求头字段
     * <p>
     * 用于携带业务ID的自定义请求头。
     */
    public final static String X_OFFICE_ID = "X-Office-Id";
    /**
     * X-App-ID 请求头字段
     * <p>
     * 用于携带应用ID的自定义请求头。
     */
    public final static String X_APP_ID = "X-App-Id";
    /**
     * X-App-Secret 请求头字段
     * <p>
     * 用于携带应用密钥的自定义请求头。
     */
    public final static String X_APP_SECRET = "X-App-Secret";
    /**
     * Access-Control-Allow-Origin 响应头字段
     * <p>
     * 指定哪些域可以访问资源。
     */
    public final static String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    /**
     * Access-Control-Allow-Methods 响应头字段
     * <p>
     * 指定可用于访问资源的HTTP方法。
     */
    public final static String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    /**
     * Access-Control-Allow-Headers 响应头字段
     * <p>
     * 指定可以使用的请求头。
     */
    public final static String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    /**
     * Access-Control-Allow-Credentials 响应头字段
     * <p>
     * 指定是否可以将请求的响应暴露给页面。
     */
    public final static String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    /**
     * Access-Control-Max-Age 响应头字段
     * <p>
     * 指定预检请求的结果能被缓存多久。
     */
    public final static String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    /**
     * Cache-Control 响应头字段
     * <p>
     * 指定响应的缓存机制。
     */
    public final static String CACHE_CONTROL = "Cache-Control";
    /**
     * X-Site-ID 请求头字段
     * <p>
     * 自定义请求头，用于标识站点ID。
     */
    public final static String X_SITE_ID = "X-SITE-ID";
    /**
     * X-Context-UUID 请求头字段
     * <p>
     * 用于微服务间传递上下文UUID的标准请求头。
     * 支持分布式链路追踪和上下文传递。
     */
    public final static String X_CONTEXT_UUID = "X-Context-UUID";

    @Contract(value = " -> fail", pure = true)
    private HttpHeaderConstant() {
        throw new UnsupportedOperationException("HttpHeaderConstant 是常量类，不允许实例化");
    }
}
