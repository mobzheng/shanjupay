package com.shanjupay.test.freemarker.controller;

import com.shanjupay.test.freemarker.model.Student;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author Administrator
 * @version 1.0
 **/
@Controller
public class FreemarkerController {

    @RequestMapping("/test1")
    public String freemarker(Map<String,Object> model, HttpServletRequest request){
        //向数据模型放数据
        model.put("name","world");
        Student stu1 = new Student();
//        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
//        stu2.setBirthday(new Date());
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        model.put("stus",stus);
        //准备map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        //向数据模型放数据
        model.put("stu1",stu1);
        //向数据模型放数据
        model.put("stuMap",stuMap);

        //测试内置对象
        request.setAttribute("attr1","test");
        HttpSession session = request.getSession();
        session.setAttribute("session1", "user1");

        //返回模板文件名称
        return "test1";
    }
}
