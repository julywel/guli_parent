package com.atguigu.guli.service.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.atguigu.guli.service.base.model.BaseEntity;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 讲师
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("edu_teacher")
//@ApiModel作用在实体类上的，value表示该实体类的备注，description表示描述什么
@ApiModel(value="Teacher对象", description="讲师")
public class Teacher extends BaseEntity {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "讲师姓名",example = "周老师")
    private String name;

    @ApiModelProperty(value = "讲师简介")
    private String intro;

    @ApiModelProperty(value = "讲师资历,一句话说明讲师")
    private String career;

    @ApiModelProperty(value = "头衔 1高级讲师 2首席讲师")
    private Integer level;

    @ApiModelProperty(value = "讲师头像")
    private String avatar;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    //不想去使用配置的全局json日期格式
    //@ApiModelProperty作用在实体类中的成员变量上，value代表这个属性的备注，example表示举得例子参数是
    @ApiModelProperty(value = "入驻时间",example = "2002-12-11")
    @JsonFormat(timezone = "UTC",pattern = "yyyy-MM-dd")
    private Date joinDate;

    //TableLogic表示逻辑删除
    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
