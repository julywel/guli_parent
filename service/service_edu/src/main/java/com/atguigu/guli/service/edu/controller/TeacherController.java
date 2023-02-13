package com.atguigu.guli.service.edu.controller;


import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
//遇见同名的controller，得再里面添加名称用以区别
@RestController("teacherController")
@RefreshScope //动态刷新nacos配置中心文件
@RequestMapping("/edu/teacher")
public class TeacherController {



}

