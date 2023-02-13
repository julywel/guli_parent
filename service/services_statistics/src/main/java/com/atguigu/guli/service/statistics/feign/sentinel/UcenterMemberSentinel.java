package com.atguigu.guli.service.statistics.feign.sentinel;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.statistics.feign.UcenterMemberFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UcenterMemberSentinel implements UcenterMemberFeign {

    @Override
    public R countRegisterNum(String day) {
        //错误日志
        log.error("熔断器被执行");
        return R.ok().data("registerNum", 0);
    }
}
