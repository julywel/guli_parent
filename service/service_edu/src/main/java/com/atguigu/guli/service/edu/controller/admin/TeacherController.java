package com.atguigu.guli.service.edu.controller.admin;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.entity.vo.TeacherQueryVo;
import com.atguigu.guli.service.edu.feign.OssFileFeign;
import com.atguigu.guli.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
//该注解为swagger2中，作用在controller上面，tags为描述信息，可以为数组
@Api(tags = "讲师管理")
@RestController("adminTeacherController")
@RequestMapping("/admin/edu/teacher")
@RefreshScope //动态刷新nacos配置中心文件
//@CrossOrigin//允许跨域
@Slf4j
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    //从其他微服务中拉取api
    @Autowired
    private OssFileFeign ossFileFeign;


    @ApiOperation(value = "所有的讲师列表")
    @GetMapping("list")
    public R listAll(){
        List<Teacher> list = teacherService.list();
        if(CollectionUtils.isEmpty(list)){
            return R.error();
        }
        return R.ok()
                .data("items",list);
    }


    //@ApiParam(value = "讲师ID")作用于接口参数上面，value表示该参数的备注
    //swagger2注释，作用在controller中的api上面，value表示该接口的备注，notes表示详细信息
    @ApiOperation(value = "根据ID删除讲师",notes = "根据ID删除讲师，逻辑删除")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam(value = "讲师ID") @PathVariable String id){
        //删除讲师头像
        teacherService.removeAvatarById(id);
        boolean result = teacherService.removeById(id);
        if (!result) {
            return R.error().message("数据不存在");
        }
        return R.ok().message("删除成功");
    }

    @ApiOperation(value="讲师分页列表")
    @GetMapping("list/{page}/{limit}")
    //required=true表示必须不能为空，为空就不能进行填写
    public R listPage(@ApiParam(value = "当前页码",required = true) @PathVariable("page") Long page,
                      @ApiParam(value = "每页记录数",required = true) @PathVariable("limit") Long limit,
                      @ApiParam(value = "查询对象") TeacherQueryVo teacherQueryVo){
        //将参数存放进入Page对象中
        //这里不设置默认值，是因为想将设置默认值交给前端进行设置
        Page<Teacher> teacherPage = new Page<>(page,limit);
        //使用service中分页进行分页操作,后面可以写一个查询对象，如果全部都需要查询可以不用写
        IPage<Teacher> pageModel = teacherService.selectPage(teacherPage,teacherQueryVo);
        List<Teacher> teachers = pageModel.getRecords();
        long total = pageModel.getTotal();//总记录数
        return R.ok().data("total",total).data("rows",teachers);
    }


    //post请求一般通过json数据（requestBody）进行传递，或者就是通过表单（@RequestParam）
    //由于创建时间，id，更新时间不用我们自己去创建,那么就得去实现自动填充
    //使用requestBody注解前端vue使用axios就得使用data这个属性
    @ApiOperation("新增讲师")
    @PostMapping("save")
    public R save(@ApiParam("讲师对象") @RequestBody Teacher teacher){
        try {
            teacherService.save(teacher);
            return R.ok().message("保存成功");
        }catch(Exception e){
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException("讲师已经存在",20088);
        }
    }

    @ApiOperation("更新讲师")
    @PutMapping("update")
    public R updateById(@ApiParam("讲师对象") @RequestBody Teacher teacher){
        //跟前端交流，提交时一定包含id
        boolean result = teacherService.updateById(teacher);
        if(!result){
            return R.error().message("数据不存在");
        }
        return R.ok().message("修改成功");
    }


    @ApiOperation("根据id获取讲师信息")
    @GetMapping("get/{id}")
    public R getById(@ApiParam("讲师对象") @PathVariable("id") String id){
        Teacher teacher = teacherService.getById(id);
        if(teacher == null){
            return R.error().message("数据不存在");
        }
        return R.ok().data("item",teacher);
    }


    @ApiOperation("根据Id列表删除讲师")
    @DeleteMapping ("batch-remove") //通过json形式传递
    public R removeRows(@ApiParam(value = "讲师ID列表",required = true) @RequestBody  List<String> idList){
        boolean result = teacherService.removeByIds(idList);
        if(result){
            return R.ok().message("删除成功");
        }
        return R.error().message("数据不存在");
    }

    @ApiOperation("根据关键字查询讲师名列表")
    @GetMapping("list/name/{key}")
    public R selectNameList(@ApiParam(value = "关键字",required = true) @PathVariable String key){
        List<Map<String,Object>> nameList = teacherService.selectNameList(key);
        return R.ok().data("nameList",nameList);
    }


    @ApiOperation("并发测试服务调用")
    @GetMapping("test_concurrent")
    public R testConcurrent(){
        log.info("test_concurrent");
//        ossFileFeign.test();
        return R.ok();
    }


    @ApiOperation("测试oss传递的")
    @GetMapping("test")
    public R test(){
        return ossFileFeign.test();
    }


    @ApiOperation("测试sentinel服务容错方法2")
    @GetMapping("/message1")
    public String message1(){
        return "message1";
    }

    @ApiOperation("测试sentinel服务容错方法2")
    @GetMapping("/message2")
    public String message2(){
        return "message2";
    }


}

