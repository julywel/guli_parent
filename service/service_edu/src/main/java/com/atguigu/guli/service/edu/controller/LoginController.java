package com.atguigu.guli.service.edu.controller;

import com.atguigu.guli.common.base.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

////@CrossOrigin
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@RequestMapping("/user")
@Api(tags = "首页")
public class LoginController {

    /**
     * 登录功能
     * @return
     */
    @PostMapping("login")
    @ApiOperation(value = "首页登录操作")
    public R login(){
        return R.ok().data("token","admin");
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("info")
    @ApiOperation(value = "验证token是否有效")
    public R info(){
        return R.ok().data("name","admin")
                .data("roles","[admin,admin]")
                .data("avatar","https://img.alicdn.com/imgextra/i3/O1CN01yV11h41EqA0UoZy0Z_!!6000000000402-2-tps-128-128.png");
    }


    /**
     * 退出
     * @return
     */
    @ApiOperation("退出操作")
    @PostMapping("logout")
    public R logout(){
        return R.ok();
    }
}
