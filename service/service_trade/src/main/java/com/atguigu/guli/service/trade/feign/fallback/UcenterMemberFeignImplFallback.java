package com.atguigu.guli.service.trade.feign.fallback;

import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.trade.feign.UcenterMemberFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UcenterMemberFeignImplFallback implements UcenterMemberFeign {
    @Override
    public MemberDto getMemberDtoByMemberId(String memberId) {
        log.error("熔断保护");
        return null;
    }
}
