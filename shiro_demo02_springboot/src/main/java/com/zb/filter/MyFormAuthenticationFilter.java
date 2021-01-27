package com.zb.filter;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @Title
 * @Author zb
 * @Description:
 *
 * Shiro框架表单认证过滤器在没有认证之前，会自动记录浏览器在当前项目中最后一次访问的任何一个页面，
 * 并且保存到Session中，当用户认证成功以后，Shiro自动跳转到这个页面，不管页面存不存在，正确与否
 * 自定表单认证过滤器，并且onLoginSuccess方法，清除保存在Session中跳转的地址
 */
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {


        @Override
        protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
                WebUtils.getAndClearSavedRequest(request);
                return super.onLoginSuccess(token, subject, request, response);
        }

        /**
         *   自定义认证过滤器重写isAccessAllowed 方法
         *      把cookie中存储从认证信息存储在 session中
         *
         *
         *    记住我的时候, 认证信息存储在cookie, 在当前的index 不用认证
         *    其他的页面 还是需要认证
         *    解决 :
         *     重写 isAccessAllowed 方法
         *     把cookie的认证信息存储到session中
         *     其他的页面能共享数据,不用在进行认证的操作
         *
         * @param request
         * @param response
         * @param mappedValue
         * @return
         */

        @Override
        protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
                //从请求中获取Shiro的 主体
                Subject subject = getSubject(request, response);
                //从主体中获取Shiro框架的Session
                Session session = subject.getSession();
                //如果主体没有认证（Session中认证）并且 主体已经设置记住我了
                if (!subject.isAuthenticated() && subject.isRemembered()) {
                        //获取主体的身份（从记住我的Cookie中获取的）
                        String principal = (String) subject.getPrincipal();
                        //将身份认证信息共享到 Session中
                        session.setAttribute("USER_IN_SESSION", principal);
                        System.out.println(" 记住我的时候  把我们的 认证信息存储到session中 ");
                }
                return subject.isAuthenticated() || subject.isRemembered();
        }
}
