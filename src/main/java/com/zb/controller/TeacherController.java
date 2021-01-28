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
@RequestMapping("teacher")
public class TeacherController {

        @RequestMapping("list")
        @RequiresPermissions("teacher:list")
        public String teacherList(){
                return "teacherPage";
        }
}
