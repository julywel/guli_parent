package com.atguigu.guli.service.edu.feign;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.feign.sentinel.OssFileFeignSentinel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

//微服务注册中心的名字
@FeignClient(value = "service-oss",fallback = OssFileFeignSentinel.class)
@Component
public interface OssFileFeign {

    @ApiOperation(value = "测试")
    //完整地址
    @GetMapping("/admin/oss/file/test")
    R test();

    @DeleteMapping("/admin/oss/file/remove")
    R removeFile(@RequestBody String url);
}
