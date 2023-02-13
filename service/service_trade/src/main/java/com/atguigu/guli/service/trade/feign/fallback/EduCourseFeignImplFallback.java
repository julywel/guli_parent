package com.atguigu.guli.service.trade.feign.fallback;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.trade.feign.EduCourseFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EduCourseFeignImplFallback implements EduCourseFeign {
    @Override
    public CourseDto getCourseDtoById(String courseId) {
        log.error("熔断保护");
        return null;
    }

    @Override
    public R updateBuyCountById(String id) {
        log.error("熔断保护");
        return null;
    }
}
