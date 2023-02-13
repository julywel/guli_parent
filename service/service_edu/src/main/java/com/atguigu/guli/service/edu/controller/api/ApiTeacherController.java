package com.atguigu.guli.service.edu.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

////@CrossOrigin
@Api(tags = "前端讲师")
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@RequestMapping("/api/edu/teacher")
@Slf4j
public class ApiTeacherController {

    @Autowired
    private TeacherService teacherService;

    @ApiOperation("讲师列表")
    @GetMapping("list")
    public R listAll(){
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Teacher::getSort);
        queryWrapper.orderByAsc(Teacher::getJoinDate);
        List<Teacher> list = teacherService.list(queryWrapper);
        return R.ok().data("items",list).message("获取讲师列表成功");

    }

    @ApiOperation("根据id查询涉及讲师的信息")
    @GetMapping("get/{id}")
    public R getTeacherById(
            @ApiParam("讲师id") @PathVariable String id
    ){
        Map<String, Object> teacherInfoById = teacherService.selectTeacherInfById(id);

        if(teacherInfoById == null){
            return R.error().message("数据不存在");
        }
        return R.ok().data("item",teacherInfoById).message("获取讲师成功");
    }



}
