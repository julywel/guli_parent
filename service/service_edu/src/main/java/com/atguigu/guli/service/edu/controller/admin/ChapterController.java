package com.atguigu.guli.service.edu.controller.admin;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Chapter;
import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.entity.vo.ChapterVo;
import com.atguigu.guli.service.edu.feign.VodVideoFeign;
import com.atguigu.guli.service.edu.service.ChapterService;
import com.atguigu.guli.service.edu.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
@Api(tags = "章节管理")
////@CrossOrigin
@Slf4j
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@RequestMapping("/admin/edu/chapter")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private VodVideoFeign vodVideoFeign;

    @ApiOperation("新增章节")
    @PostMapping("save")
    public R save(
            @ApiParam(value = "章节对象",required = true)
            @RequestBody Chapter chapter){
        boolean result = chapterService.save(chapter);
        if(result){
            return R.ok().message("保存成功");
        }
        return R.error().message("保存失败");
    }

    @ApiOperation("根据id查询章节")
    @GetMapping("get/{id}")
    public R getById(
            @ApiParam(value="章节id", required = true)
            @PathVariable String id){

        Chapter chapter = chapterService.getById(id);
        if (chapter != null) {
            return R.ok().data("item", chapter);
        } else {
            return R.error().message("数据不存在");
        }
    }


    @ApiOperation("根据id修改章节")
    @PutMapping("update")
    public R updateById(
            @ApiParam(value="章节对象", required = true)
            @RequestBody Chapter chapter){

        boolean result = chapterService.updateById(chapter);
        if (result) {
            return R.ok().message("修改成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据ID删除章节")
    @DeleteMapping("remove/{id}")
    public R removeById(
            @ApiParam(value = "章节ID",required = true)
            @PathVariable String id){

        /*这里可以将他写入videoService的业务中进行删除*/
        //此处调用vod中的删除视频文件的接口
        LambdaQueryWrapper<Video> videoQueryWrapper = new LambdaQueryWrapper<>();
        videoQueryWrapper.eq(Video::getChapterId,id);
        videoService.remoeVideoByIds(videoQueryWrapper);
        //删除章节
        boolean result = chapterService.removeChapterById(id);
        if(result){
            return R.ok().message("删除成功");
        }
        return R.error().message("删除失败");
    }

    @ApiOperation("嵌套章节数据列表")
    @GetMapping("nested-list/{courseId}")
    public R nestedListCourseId(
            @ApiParam(value = "课程ID",required = true)
            @PathVariable String courseId){
        List<ChapterVo> chapterVoList = chapterService.nestedList(courseId);
        return R.ok().data("items",chapterVoList);
    }

}

