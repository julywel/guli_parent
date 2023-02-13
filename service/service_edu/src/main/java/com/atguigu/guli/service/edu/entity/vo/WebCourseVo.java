package com.atguigu.guli.service.edu.entity.vo;

import com.atguigu.guli.service.edu.entity.Chapter;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.CourseDescription;
import com.atguigu.guli.service.edu.entity.Video;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WebCourseVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private BigDecimal price;
    private Integer lessonNum;
    private String cover;
    private Long buyCount;
    private Long viewCount;
    private String description;
    private String teacherId;
    private String teacherName;
    private String intro;
    private String avatar;
    private String subjectLevelOneId;
    private String subjectLevelOne;
    private String subjectLevelTwoId;
    private String subjectLevelTwo;
}
