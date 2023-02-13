package com.atguigu.guli.service.ucenter.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mobile;
    private String password;

}
