package com.simplegame.deploy.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月16日 下午3:26:01
 *
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(Map<String, Object> model) {
        
        return "index";
    }
    
}
