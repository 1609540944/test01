package com.zb.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.Arrays;
import java.util.List;

/**
 * @Title
 * @Author zb
 * @Description:
 */
public class CustomerRealm extends AuthorizingRealm {
        @Override
        protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
                System.out.println("CustomerRealm.doGetAuthorizationInfo");
                String username = (String) principalCollection.getPrimaryPrincipal();

                //去数据库查询数据 模拟数据库操作
                /**
                 * 权限都是:资源  操作:具体对象
                 */
                //权限数据源
                List<String> perms = Arrays.asList("stu:list", "teacher:list", "emp:list");

                //把数据返回给授权信息对象
                SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
                //吧授权对象设置到信息对象中
                simpleAuthorizationInfo.addStringPermissions(perms);
                return simpleAuthorizationInfo;
        }

        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
                /**
                 * 认证
                 *      获取token 唯一标识
                 *      从数据库中获取user信息
                 *      密码/盐值
                 *      返回 认证信息
                 */
                System.out.println("CustomerRealm.AuthenticationInfo");
                String user = (String) authenticationToken.getPrincipal();
                List<String> names = Arrays.asList("jack", "rose", "scott");
                if (!names.contains(user)){
                        return null;
                }
                //加密后的密码
                String pwd = "dcd171b1ae8d58251fbacbf91a89c82d";
                // 盐值
                ByteSource salt = ByteSource.Util.bytes("abc");


                //返回一个认证信息
                SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user,pwd,salt,this.getName());

                return simpleAuthenticationInfo;
        }
}
