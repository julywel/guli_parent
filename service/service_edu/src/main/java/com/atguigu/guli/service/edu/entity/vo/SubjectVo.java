package com.atguigu.guli.service.edu.entity.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel("树形结构课程分类")
@Data
public class SubjectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private Integer sort;

    private List<SubjectVo> children = new ArrayList<>();
}
