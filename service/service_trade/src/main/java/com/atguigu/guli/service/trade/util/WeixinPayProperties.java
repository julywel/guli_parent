package com.atguigu.guli.service.trade.util;


import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "weixin.pay")
public class WeixinPayProperties {
    private String appId;
    private String partner;
    private String partnerKey;
    private String notifyUrl;
}
