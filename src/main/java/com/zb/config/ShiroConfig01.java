package com.zb.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.zb.filter.MyFormAuthenticationFilter;
import com.zb.realm.CustomerRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Title
 * @Author zb
 * @Description:
 * shiro 集成 springboot 的配置类 环境搭建
 *
 *    ShiroFilterFactoryBean 的配置的细节:
 *
 *     web应用中的资源：
 *
 *       01  静态资源基本要放行js,css,图片等
 *
 *       02  需要认证（登录）但是不需要权限，能访问的资源
 *              1)资源需要登录访问，跳转登录页面
 *              2)资源不要登录访问，直接放行
 *
 *      03   用户登录以后，还需要判断用户是否拥有有权限访问的资源
 *                 1)有：直接放行访问的资源
 *                 2)没有：跳转没有提示的页面
 *
 *
 *         shirofilter 中有中的拦截器.可以拦截指定的资源
 *
 *         shirofilter 常见的过滤器
 *
 *         过滤器使用别名                                   过滤器对应的类
 *         01  anon	org.apache.shiro.web.filter.authc.AnonymousFilter
 *                  不拦截,直接放行 （要放行js,css,图片等） 就用这个来配置
 *
 *
 *         02  authc	org.apache.shiro.web.filter.authc.FormAuthenticationFilter
 *                   表单过滤器  拦截请求， 跳转登录页面
 *
 *       匿名过滤器的配置
 *
 *       没有认证跳转到 登录页面的url 的配置
 *         shiroFilterFactoryBean.setLoginUrl("/user/loginPage");
 *
 */
@Configuration
public class ShiroConfig01 {
        // 这里需要配置 shirofilter
        // 这里配置 shirofilter 继承中  提供了  shirofilterFactoryBean 工程类型  可以创建 shirofilter类注入ioc容器中
        // ioc的时候  xxxFactoryBean 工厂类型 整合第三方的Java类注入ioc中容器中

        @Bean
        public ShiroFilterFactoryBean shiroFilterFactoryBean(){
                ShiroFilterFactoryBean shiroFilterFactoryBean=new ShiroFilterFactoryBean();
                //把securityManager设置到 shiroFilterFactoryBean
                shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager());

                //没有认证 访问都会跳转到默认的login.jsp
                //修改默认URL
                shiroFilterFactoryBean.setLoginUrl("/user/loginPage");
                //设置登录成功后的跳转页面
                shiroFilterFactoryBean.setSuccessUrl("/index");

                //如果授权失败，跳转到/unauthorized.html红色背景部分
                shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized.html");

                //  Map<String, Filter> filters;
                // key 是filter的别名  value就是具体的filter
                Map<String, Filter> filters =new HashMap<>();
                // 可以要是这个 logout  把系统在filter 替换成自己的
                filters.put("logout",logoutFilter());
                filters.put("authc",myFormAuthenticationFilter());

                shiroFilterFactoryBean.setFilters(filters);


                //配置shirofilter
                //Map<key,value>
                //key 拦截的访问资源 一般是url
                //value  过滤器的别名称
                HashMap<String, String> filterChainDefinitionMap = new HashMap<>();

                //拦截规则配置
                //从上往下配置
                // 如果上面的资源匹配 了 ,后面的配置不执行
                filterChainDefinitionMap.put("/images/a.jpg","anon");

                //配置匿名过滤器 静态资源不用拦截
                filterChainDefinitionMap.put("/images/**","anon");
                filterChainDefinitionMap.put("/css/**","anon");
                filterChainDefinitionMap.put("/favicon.ico","anon");

                //授权 权限管理
//                filterChainDefinitionMap.put("/stu/list","perms[stu:list]");
//                filterChainDefinitionMap.put("/teacher/list","perms[teacher:list]");
//                filterChainDefinitionMap.put("/emp/list","perms[emp:update]");

                //退出跳转到登录页面的请求放行
                filterChainDefinitionMap.put("/user/toLoginPage","anon");
                /*记住我过滤器
                *  管理浏览器后 访问index 会自动登录
                *     访问index的时候 会把cookie存储的认证信息读取处理 进行认证操作
                * */
                filterChainDefinitionMap.put("/index","user");


                // 登出的过滤器的配置
                LogoutFilter log;
                filterChainDefinitionMap.put("/logout","logout");
                //配置表单拦截器
                //所有的请求都会被拦截 跳转到登录页面 (默认是login.jsp)
                filterChainDefinitionMap.put("/**","authc");


                //设置过滤器的拦截规则
                shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

                return shiroFilterFactoryBean;
        }


        //获取securityManager
        //之前 不是web项目  获取securityManager  SecurityManager securityManager=factory.getInstance()
        //springboot 继承shiro  获取securityManager   securityManager===>DefaultWebSecurityManager
        @Bean
        public DefaultWebSecurityManager defaultWebSecurityManager(){
                DefaultWebSecurityManager defaultWebSecurityManager=new DefaultWebSecurityManager();
                //设置自定义realm
                defaultWebSecurityManager.setRealm(customerRealm());

                //设置缓存
                defaultWebSecurityManager.setCacheManager(cacheManager());

                //设置session时间
                defaultWebSecurityManager.setSessionManager(sessionManager());

                //设置 记住我（cookie）
                defaultWebSecurityManager.setRememberMeManager(rememberMeManager());

                return defaultWebSecurityManager;
        }

        //自定义realm
        @Bean
        public CustomerRealm customerRealm(){
                CustomerRealm customerRealm=new CustomerRealm();
                //给realm设置加密器
                customerRealm.setCredentialsMatcher(credentialsMatcher());
                return customerRealm;
        }

        //配置加密器
        @Bean
        public HashedCredentialsMatcher credentialsMatcher(){
                HashedCredentialsMatcher hashedCredentialsMatcher=new HashedCredentialsMatcher();

                //加密规则
                hashedCredentialsMatcher.setHashAlgorithmName("md5");
                //散列次数
                hashedCredentialsMatcher.setHashIterations(10);
                return hashedCredentialsMatcher;
        }

        //配置shiro语法,让Thymeleaf支持shiro标签
        @Bean
        public ShiroDialect shiroDialect() {
                return new ShiroDialect();
        }

        /**
         *  01 创建一个logoutfiilter
         *  02 设置 一个 页面跳转的url
         *
         */
        public LogoutFilter logoutFilter(){

                LogoutFilter logoutFilter = new LogoutFilter();

                // 页面 跳转 (退出的操作 实现页面的跳转 )
                logoutFilter.setRedirectUrl("/user/toLoginPage");

                return logoutFilter;
        }

        /*清除保存在Session中跳转的地址*/
        @Bean
        public MyFormAuthenticationFilter myFormAuthenticationFilter(){
                return new MyFormAuthenticationFilter();
        }


        //设置Shiro框架对注解支持
        @Bean
        public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
                AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
                authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager());
                return authorizationAttributeSourceAdvisor;
        }

        @Bean
        public SimpleMappingExceptionResolver simpleMappingExceptionResolver(){
                Properties properties = new Properties();
                // 如果是这个错误 发送  这个请求  /unauthorized
                // 注意 ： 01  模板下面存在这个页面   02 过滤器放行  /unauthorized 请求
                properties.put("org.apache.shiro.authz.UnauthorizedException","/unauthorized");
                SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
                simpleMappingExceptionResolver.setExceptionMappings(properties);

                return simpleMappingExceptionResolver;
        }

        /*设置Spring框架支持集成其他框架可以使用AOP*/
        @Bean
        public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
                DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
                /*设置可以让Shiro框架使用AOP为表现层创建代理（Shiro权限判断的注解全部在表现层）*/
                autoProxyCreator.setProxyTargetClass(true);
                return autoProxyCreator;
        }



        /*配置缓存管理器*/
        @Bean
        public CacheManager cacheManager(){
                EhCacheManager ehCacheManager = new EhCacheManager();
                //设置自定义缓存策略的配置文件
                //ehCacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
                return  ehCacheManager;
        }

        /*配置会话管理器*/
        @Bean
        public SessionManager sessionManager(){
                DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
                //设置 session的 超时   milliseconds
                // session 1800秒钟销毁  认证信息在session中销毁了  再次访问index 页面 认证信息 要重写登录
                sessionManager.setGlobalSessionTimeout(1000 * 1800);
                //  把 js的sessionidurl重写去掉
                sessionManager.setSessionIdUrlRewritingEnabled(false);
                return  sessionManager;
        }


        /*配置记住我管理器
        *
        * 配置cookie
        * */
        @Bean
        public RememberMeManager rememberMeManager(){
                // cookie的管理器
                CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
                // CookieRememberMeManager 内部操作的是 simpleCookie
                // simpleCookie 继承是 cookie (在javaweb的cookie上进行了增强)

                //设置Cookie信息
                cookieRememberMeManager.setCookie(cookie());
                cookieRememberMeManager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
                return cookieRememberMeManager;
        }

        //创建Cookie对象
        @Bean
        public Cookie cookie(){
                SimpleCookie cookie = new SimpleCookie("rememberMe");
                //仅仅http协议访问
                cookie.setHttpOnly(true);
                //设置cookie的保存时间 7天
                cookie.setMaxAge(3600 * 24 * 7);

                return cookie;
        }


}
