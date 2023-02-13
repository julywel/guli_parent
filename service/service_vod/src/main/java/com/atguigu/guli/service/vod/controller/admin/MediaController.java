package com.atguigu.guli.service.vod.controller.admin;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

//@CrossOrigin
@Slf4j
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@Api(tags = "视频管理")
@RequestMapping("/admin/vod/media")
public class MediaController {

    @Autowired
    private VideoService videoService;

    @ApiOperation("上传视频")
    @PostMapping("upload")
    public R uploadVideo(@ApiParam(name = "file",value = "文件",required = true)
                             @RequestParam("file") MultipartFile file){
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String videoId = videoService.uploadVideo(inputStream, originalFilename);
            return R.ok().message("视频上传成功").data("videoId",videoId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }
    }


    @ApiOperation("删除课时时删除视频")
    @DeleteMapping("delete")
    public R deleteVideoById1(@ApiParam(value = "视频ID",required = true)
                                 @RequestBody String videoId){
        videoService.deleteVideoByIds(videoId);
        return R.ok().message("删除成功");
    }


    @ApiOperation("单独删除视频")
    @DeleteMapping("delete/{id}")
    public R deleteVideoById2(@ApiParam(value = "视频ID",required = true)
                             @PathVariable String id){
        videoService.deleteVideoByIds(id);
        return R.ok().message("删除成功");
    }



}
