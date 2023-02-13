package com.atguigu.guli.service.edu.controller;


import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 评论 前端控制器
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@RequestMapping("/edu/comment")
public class CommentController {

}

