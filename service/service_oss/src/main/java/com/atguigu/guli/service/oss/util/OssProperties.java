package com.atguigu.guli.service.oss.util;


import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Accessors(chain = true)
@Data
@Component
//把前缀都为aliyun.oss得内容读取
@ConfigurationProperties(prefix = "aliyun.oss") //能够从配置文件里面自动进行读取信息
public class OssProperties {

    private String endPoint;
    private String keyid;
    private String keysecret;
    private String bucketname;

}
