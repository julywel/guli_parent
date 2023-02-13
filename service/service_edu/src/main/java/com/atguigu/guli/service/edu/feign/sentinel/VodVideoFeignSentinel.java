package com.atguigu.guli.service.edu.feign.sentinel;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.service.edu.feign.VodVideoFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VodVideoFeignSentinel implements VodVideoFeign {
    @Override
    public R deleteVideoById1(String videoId) {
        return R.setResult(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
    }
}
