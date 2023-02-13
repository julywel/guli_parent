package com.atguigu.guli.service.ucenter.controller.api;


import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.common.base.util.JwtInfo;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.ucenter.entity.vo.LoginVo;
import com.atguigu.guli.service.ucenter.entity.vo.RegisterVo;
import com.atguigu.guli.service.ucenter.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import com.atguigu.guli.common.base.result.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author TJS
 * @since 2022-11-20
 */
//@CrossOrigin
@Api(tags = "注册管理")
@Slf4j
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@RequestMapping("/api/ucenter/member")
public class ApiMemberController {

    @Autowired
    private MemberService memberService;


    @ApiOperation("注册用户")
    @PostMapping("register")
    public R register(
            @ApiParam(value = "注册值",required = true) @RequestBody RegisterVo registerVo){


        memberService.register(registerVo);
        return R.ok().message("注册成功");
    }


    @ApiOperation("会员登录")
    @PostMapping("login")
    public R login(@ApiParam("手机号与密码") @RequestBody LoginVo loginVo){
        String token = memberService.login(loginVo);
        return R.ok().data("token",token).message("登陆成功");
    }

    @ApiOperation("根据token信息获取登录信息")
    @GetMapping("get-login-info")
    public R getLoginInfo(HttpServletRequest request){
        try {
            JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);

            return R.ok().data("userInfo",jwtInfo);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
        }
    }

    @ApiOperation("根据会员id查询会员信息")
    @GetMapping("inner/get-member-dto/{memberId}")
    public MemberDto getMemberDtoByMemberId(
            @ApiParam(value = "会员ID", required = true)
            @PathVariable String memberId){
        MemberDto memberDto = memberService.getMemberDtoByMemberId(memberId);
        return memberDto;
    }


}

