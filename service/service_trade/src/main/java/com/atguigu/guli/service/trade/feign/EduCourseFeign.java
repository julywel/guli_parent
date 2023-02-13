package com.atguigu.guli.service.trade.feign;

import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.trade.feign.fallback.EduCourseFeignImplFallback;
import com.atguigu.guli.common.base.result.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "service-edu",fallback = EduCourseFeignImplFallback.class)
public interface EduCourseFeign {

    @GetMapping("/api/edu/course/inner/get-course-dto/{courseId}")
    CourseDto getCourseDtoById(@PathVariable("courseId") String courseId);

    @GetMapping("/api/edu/course/inner/update-buy-count/{id}")
    R updateBuyCountById(@PathVariable("id") String id);
}
