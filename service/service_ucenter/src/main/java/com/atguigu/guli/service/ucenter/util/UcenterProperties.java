package com.atguigu.guli.service.ucenter.util;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "wx.open")
public class UcenterProperties {

    private String appId;
    private String appSecret;
    private String redirectUri;
}
