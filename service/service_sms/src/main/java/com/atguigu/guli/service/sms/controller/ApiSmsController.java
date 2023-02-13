package com.atguigu.guli.service.sms.controller;


import com.aliyuncs.exceptions.ClientException;
import com.atguigu.guli.common.base.util.FormUtils;
import com.atguigu.guli.common.base.util.RandomUtils;
import com.atguigu.guli.service.sms.service.SmsService;
import com.atguigu.guli.common.base.result.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

//@CrossOrigin
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@Slf4j
@Api(tags = "短信管理")
@RequestMapping("/api/sms")
public class ApiSmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;


    @ApiOperation("验证码")
    @GetMapping("send/{mobile}")
    public R getCode(
            @ApiParam(value = "手机号码",required = true)@PathVariable String mobile) throws ClientException {
        //校验手机号是否为标准格式
        if(StringUtils.isEmpty(mobile)) {
            return R.error().message("手机号不能为空").code(28001);
        }
        if(!FormUtils.isMobile(mobile)) {
            return R.error().message("手机号格式错误").code(28100);
        }

        //生成验证码
        String checkCode = RandomUtils.getSixBitRandom();

        //这里之所以抛是因为需要将那个信息抛出，因为存在不同情况
        // 发送验证码
//        smsService.send(mobile,checkCode);

        //存储验证码到redis
        redisTemplate.opsForValue().set(mobile,checkCode,5, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");
    }


}
