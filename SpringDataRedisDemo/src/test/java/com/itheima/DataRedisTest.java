package com.itheima;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-redis.xml")
public class DataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test1(){


        redisTemplate.boundValueOps("name").set("张飞");

    }

    @Test
    public void test2(){


        String name = (String) redisTemplate.boundValueOps("name").get();
        System.out.println(name);

    }

    @Test
    public void delete(){
        redisTemplate.delete("name");



    }

    @Test
    public void setValue(){
        redisTemplate.boundSetOps("nameset").add("曹操");
        redisTemplate.boundSetOps("nameset").add("刘备");
        redisTemplate.boundSetOps("nameset").add("孙权");



    }
    @Test
    public void getSetValue(){

        Set nameset = redisTemplate.boundSetOps("nameset").members();
        System.out.println(nameset);


    }
    @Test
    public void deleSetValue(){
       // redisTemplate.boundSetOps("nameset").remove("孙权");

        redisTemplate.delete("nameset");

    }

    @Test
    public void listValue(){
        redisTemplate.boundListOps("nameList").rightPush("刘备");
        redisTemplate.boundListOps("nameList").rightPush("关羽");
        redisTemplate.boundListOps("nameList").rightPush("张飞");



    }
    @Test
    public void leftlistValue(){
        redisTemplate.boundListOps("leftnameList").leftPush("刘备");
        redisTemplate.boundListOps("leftnameList").leftPush("关羽");
        redisTemplate.boundListOps("leftnameList").leftPush("张飞");
        redisTemplate.boundListOps("leftnameList").rightPush("刘备");
        redisTemplate.boundListOps("leftnameList").rightPush("关羽");
        redisTemplate.boundListOps("leftnameList").rightPush("张飞");



    }
    @Test
    public void getlistValue(){
       // String leftnameList = (String) redisTemplate.boundListOps("leftnameList").leftPop();

        List leftnameList = redisTemplate.boundListOps("leftnameList").range(0, 10);
        System.out.println(leftnameList);


    }
    @Test
    public void getlistValue2(){
        String leftnameList = (String) redisTemplate.boundListOps("leftnameList").index(1);
        System.out.println(leftnameList);
    }
    @Test
    public void delelistValue(){

        redisTemplate.delete("leftnameList");


    }
    @Test
    public void hashValue(){

        redisTemplate.boundHashOps("hashName").put("a", "唐僧");
        redisTemplate.boundHashOps("hashName").put("b", "悟空");
        redisTemplate.boundHashOps("hashName").put("c", "八戒");
        redisTemplate.boundHashOps("hashName").put("d", "沙僧");


    }
    @Test
    public void getValue(){

       //提取所有的key
        Set hashName = redisTemplate.boundHashOps("hashName").keys();
        System.out.println(hashName);
    }
    @Test
    public void getAllValue(){

        //提取所有的key
        //List hashName =  redisTemplate.boundHashOps("hashName").values();
        String hashName = (String) redisTemplate.boundHashOps("hashName").get("a");
        System.out.println(hashName);
    }

    @Test
    public void moveValue(){
        redisTemplate.boundHashOps("hashName").delete("a");
    }
}
