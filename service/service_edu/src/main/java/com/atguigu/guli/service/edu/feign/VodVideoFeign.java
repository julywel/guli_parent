package com.atguigu.guli.service.edu.feign;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.feign.sentinel.VodVideoFeignSentinel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-vod",fallback = VodVideoFeignSentinel.class)
@Component
public interface VodVideoFeign {

    //删除单个视频
    //路径要写类上+请求的路径
    @DeleteMapping("/admin/vod/media/delete")
    R deleteVideoById1(@RequestBody String videoId);

}
