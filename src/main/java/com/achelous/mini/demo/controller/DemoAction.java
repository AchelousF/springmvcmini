package com.achelous.mini.demo.controller;

import com.achelous.mini.demo.service.IDemoService;
import com.achelous.mini.spring.annotation.Autowire;
import com.achelous.mini.spring.annotation.Controller;
import com.achelous.mini.spring.annotation.RequestMapping;
import com.achelous.mini.spring.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: fanJiang
 * @Date: Create in 10:59 2018/4/22
 */
@Controller
@RequestMapping("/demo")
public class DemoAction {

    @Autowire
    IDemoService demoService;

    @RequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @RequestParam("name") String name) {
        String s = demoService.get(name);


        System.out.println(s);
    }

}
