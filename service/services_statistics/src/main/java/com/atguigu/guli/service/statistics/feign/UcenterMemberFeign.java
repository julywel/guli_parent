package com.atguigu.guli.service.statistics.feign;

import com.atguigu.guli.service.statistics.feign.sentinel.UcenterMemberSentinel;
import com.atguigu.guli.common.base.result.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-ucenter", fallback = UcenterMemberSentinel.class)
@Component
public interface UcenterMemberFeign {

    @GetMapping(value = "/admin/ucenter/member/count-register-num/{day}")
    R countRegisterNum(@PathVariable("day") String day);
}
