package com.zb.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Title
 * @Author zb
 * @Description:
 */
@Controller
@RequestMapping("emp")
public class EmpController {

        @RequestMapping("list")
        @RequiresPermissions("emp:e,plist")
        public String empList(){
                return "empPage";
        }
}
