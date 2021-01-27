package com.zb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Title
 * @Author zb
 * @Description:
 */
@Controller
public class IndexController {
        @RequestMapping("index")
        public String index(){
                return "index";
        }
}
