package com.atguigu.guli.service.sms.util;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "aliyun.sms")
@Component
@Data
@Accessors(chain = true)
public class SmsProperties {


//    regionId: cn-hangzhou
//    keyid: LTAI5tAxq7VWc3LGvPLo3PTs
//    keysecret: xuDPe2CSgTorVmLdavGaxWpK3JH6VR
//    templateCode: SMS_259810253
//    signName: 你的短信模板签名
    private String regionId;

    private String keyid;

    private String keysecret;

    private String templateCode;

    private String signName;

}
