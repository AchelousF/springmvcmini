package com.achelous.mini.demo.service.impl;

import com.achelous.mini.demo.service.IDemoService;
import com.achelous.mini.spring.annotation.Service;

/**
 * @Auther: fanJiang
 * @Date: Create in 11:03 2018/4/22
 */
@Service
public class DemoServiceImpl implements IDemoService {
    public String get(String name) {
        return "my name is" + name;
    }
}
