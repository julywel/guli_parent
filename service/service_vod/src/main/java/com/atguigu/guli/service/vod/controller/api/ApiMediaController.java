package com.atguigu.guli.service.vod.controller.api;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin
@Slf4j
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@Api(tags = "视频管理")
@RequestMapping("/api/vod/media")
public class ApiMediaController {

    @Autowired
    private VideoService videoService;

    @ApiOperation("获取阿里云视频播放凭证")
    @GetMapping("get-play-auth/{videoSourceId}")
    public R getVideoPlayAuth(
            @ApiParam("视频资源Id") @PathVariable String videoSourceId){
        try {
            String playAuth = videoService.getPlayAuth(videoSourceId);
            return R.ok().data("playAuth",playAuth).message("获取凭证成功");
        }catch (Exception e){
            log.error(ExceptionUtils.getMessage(e));
            return R.setResult(ResultCodeEnum.FETCH_PLAYAUTH_ERROR);
        }
    }
}
