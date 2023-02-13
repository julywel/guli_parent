package com.atguigu.guli.service.trade.controller;


import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author TJS
 * @since 2022-11-28
 */
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@RequestMapping("/trade/pay-log")
public class PayLogController {

}

