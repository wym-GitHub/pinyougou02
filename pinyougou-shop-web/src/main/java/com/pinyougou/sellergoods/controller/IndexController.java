package com.pinyougou.sellergoods.controller;

import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("index")
public class IndexController {
    @RequestMapping("getName")
    public Result getName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return new Result(true,name);


    }
}
