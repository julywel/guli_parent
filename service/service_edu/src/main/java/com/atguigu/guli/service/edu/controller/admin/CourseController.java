package com.atguigu.guli.service.edu.controller.admin;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.entity.form.CourseInfoForm;
import com.atguigu.guli.service.edu.entity.vo.CoursePublishVo;
import com.atguigu.guli.service.edu.entity.vo.CourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.CourseVo;
import com.atguigu.guli.service.edu.entity.vo.TeacherQueryVo;
import com.atguigu.guli.service.edu.feign.VodVideoFeign;
import com.atguigu.guli.service.edu.service.CourseDescriptionService;
import com.atguigu.guli.service.edu.service.CourseService;
import com.atguigu.guli.service.edu.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@RestController
@RefreshScope //动态刷新nacos配置中心文件
////@CrossOrigin //允许跨域
@Slf4j
@Api(tags = "课程管理")
@RequestMapping("/admin/edu/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private VodVideoFeign vodVideoFeign;

    @Autowired
    private CourseDescriptionService courseDescriptionService;

    @ApiOperation("新增课程")
    @PostMapping("/save-course-info")
    public R saveCourseInfo(
            @ApiParam(value = "课程基本信息",required = true)
            @RequestBody CourseInfoForm courseInfoForm
    ){
        String courseId = courseService.saveCourseInfo(courseInfoForm);
        return R.ok().data("courseId",courseId).message("保存成功");
    }

    @ApiOperation("根据ID查询课程")
    @GetMapping("course-info/{id}")
    public R getById(
            @ApiParam("课程ID") @PathVariable String id){
        CourseInfoForm courseInfoForm = courseService.getCourseInfoById(id);
        if(courseInfoForm != null){
            return R.ok().data("item",courseInfoForm);
        }
        return R.ok().message("数据不存在");
    }

    @ApiOperation("根据ID更新课程")
    @PutMapping("update-course-info")
    public R updateCourseInfo(
            @ApiParam("课程基本信息") @RequestBody CourseInfoForm courseInfoForm){
        courseService.updateCourseInfo(courseInfoForm);
        return R.ok().message("更新成功");
    }

    @ApiOperation(value="课程分页列表")
    @GetMapping("list/{page}/{limit}")
    //required=true表示必须不能为空，为空就不能进行填写
    public R listPage(@ApiParam(value = "当前页码",required = true) @PathVariable("page") Long page,
                      @ApiParam(value = "每页记录数",required = true) @PathVariable("limit") Long limit,
                      @ApiParam(value = "课程列表查询对象")CourseQueryVo courseQueryVo){

        IPage<CourseVo> pageModel = courseService.selectPage(page,limit,courseQueryVo);
        List<CourseVo> courses = pageModel.getRecords();
        long total = pageModel.getTotal();//总记录数
        return R.ok().data("total",total).data("rows",courses);
    }

    //@ApiParam(value = "讲师ID")作用于接口参数上面，value表示该参数的备注
    //swagger2注释，作用在controller中的api上面，value表示该接口的备注，notes表示详细信息
    @ApiOperation(value = "根据ID删除课程",notes = "根据ID删除课程，非逻辑删除")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam(value = "课程ID") @PathVariable String id){

        //调用vod中的删除文件的接口
        LambdaQueryWrapper<Video> videoQueryWrapper = new LambdaQueryWrapper<>();
        videoQueryWrapper.eq(Video::getCourseId,id);
        videoService.remoeVideoByIds(videoQueryWrapper);


        //删除课程封面
        courseService.removeCoverById(id);
        //删除课程
        boolean result = courseService.removeCourseById(id);
        courseDescriptionService.removeById(id);
        if (!result) {
            return R.error().message("数据不存在");
        }
        return R.ok().message("删除成功");
    }

    @ApiOperation("根据ID获取课程发布信息")
    @GetMapping("course-publish/{id}")
    public R getCoursePublishVoById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String id){

        CoursePublishVo coursePublishVo = courseService.getCoursePublishVoById(id);
        if (coursePublishVo != null) {
            return R.ok().data("item", coursePublishVo);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @PutMapping("publish-course/{id}")
    @ApiOperation("根据id发布课程")
    public R publishCourseById(@ApiParam(value = "课程ID",required = true)
                               @PathVariable String id){
        boolean result = courseService.publishCourseById(id);

        if(result){
            return R.ok().message("发布成功");
        }
        return R.error().message("数据不存在");
    }

}

