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
@RequestMapping("stu")
public class StuController {
        @RequestMapping("list")
        @RequiresPermissions("stu:list")
        public String StuList(){

                return "studenPage";

        }
}
