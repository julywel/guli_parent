package com.atguigu.guli.service.edu.controller.api;


import com.atguigu.guli.common.base.util.JwtInfo;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.edu.entity.vo.CourseCollectVo;
import com.atguigu.guli.service.edu.service.CourseCollectService;
import com.atguigu.guli.common.base.result.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 课程收藏 前端控制器
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@Api(tags = "收藏管理")
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@Slf4j
////@CrossOrigin
@RequestMapping("/api/edu/course-collect")
public class ApiCourseCollectController {

    @Autowired
    private CourseCollectService courseCollectService;

    @ApiOperation("根据课程id判断是否已被收藏")
    @GetMapping("auth/is-collect/{courseId}")
    public R isCollect(
            @ApiParam(value = "课程id",required = true) @PathVariable String courseId,
            HttpServletRequest request){
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        boolean result = courseCollectService.isCollectByCourseId(courseId,jwtInfo.getId());
        return R.ok().data("isCollect",result);
    }

    @ApiOperation("新增收藏")
    @PostMapping("auth/save/{courseId}")
    public R save(
            @ApiParam(value = "课程id",required = true) @PathVariable String courseId,
            HttpServletRequest request){
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        courseCollectService.save(courseId,jwtInfo.getId());
        return R.ok();
    }

    @ApiOperation("根据用户Id获取收藏列表")
    @GetMapping("auth/list")
    public R list(HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        List<CourseCollectVo> courseCollectVoList = courseCollectService.getCourseCollectVoByMemberId(jwtInfo.getId());
        return R.ok().data("items",courseCollectVoList);
    }

    @ApiOperation("取消收藏")
    @DeleteMapping("auth/remove/{courseId}")
    public R remove(
            @ApiParam(name = "courseId", value = "课程id", required = true)
            @PathVariable String courseId,
            HttpServletRequest request){
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        boolean isDelete = courseCollectService.removeByCourseId(courseId,jwtInfo.getId());
        if(isDelete){
            return R.ok().message("已取消收藏");
        }
        return R.error().message("取消失败");
    }

}

