package com.achelous.mini.demo.controller;

import com.achelous.mini.spring.annotation.Controller;
import com.achelous.mini.spring.annotation.RequestMapping;

/**
 * @Auther: fanJiang
 * @Date: Create in 11:02 2018/4/22
 */
@Controller
@RequestMapping("myAction")
public class MyAction {

    @RequestMapping("index.html")
    public void index(){

    }
}
