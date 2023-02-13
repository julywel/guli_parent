package com.atguigu.guli.service.trade.feign;

import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.trade.feign.fallback.UcenterMemberFeignImplFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "service-ucenter",fallback = UcenterMemberFeignImplFallback.class)
public interface UcenterMemberFeign {


    @GetMapping("/api/ucenter/member/inner/get-member-dto/{memberId}")
    MemberDto getMemberDtoByMemberId(@PathVariable("memberId") String memberId);
}
