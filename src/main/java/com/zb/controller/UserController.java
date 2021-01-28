package com.zb.controller;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Title
 * @Author zb
 * @Description:
 */
@Controller
@RequestMapping("user")
public class UserController {
        /**
         *  到 login页面 的请求
         *
         *  登录失败了后 :
         *    FormAuthenticationFilter 类中的源码:
         *
         *      protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
         *         String className = ae.getClass().getName(); // 获取异常类的信息
         *         // getFailureKeyAttribute() == >  public static final String DEFAULT_ERROR_KEY_ATTRIBUTE_NAME = "shiroLoginFailure";
         *         request.setAttribute(getFailureKeyAttribute(), className);
         *     }
         *
         *     登录失败后 req中发送了一个错误新
         *         错误新的key : shiroLoginFailure
         *          value    : 登录的异常错误
         *    把登录失败的错误信息发送到login页面
         * @return
         */
        @RequestMapping("loginPage")
        public String loginPage(HttpServletRequest request, Model m){

                String shiroLoginFailure = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);

                if (shiroLoginFailure != null){

                        // 不知道账号的错误的异常
                        if (UnknownAccountException.class.getName().equals(shiroLoginFailure)){
                                m.addAttribute("msg","账号不对");
                        }
                        // 不知道账号的错误
                        if (IncorrectCredentialsException.class.getName().equals(shiroLoginFailure)){
                                m.addAttribute("msg","密码不对");
                        }
                }
                return "login";
        }

        /**
         *  这个请求就是 登出 跳转到 登录页面的处理
         *
         * @return
         */
        @RequestMapping("toLoginPage")
        public String toLoginPage(){
                return "login";
        }

}
