package com.atguigu.guli.service.sms.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.sms.service.SmsService;
import com.atguigu.guli.service.sms.util.SmsProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SmsProperties smsProperties;

    @Override
    public void send(String mobile, String checkCode) throws ClientException {
        //发送短信
        DefaultProfile profile = DefaultProfile.getProfile(smsProperties.getRegionId(),
                smsProperties.getKeyid(),
                smsProperties.getKeysecret());

        IAcsClient client = new DefaultAcsClient(profile);


        SendSmsRequest request = new SendSmsRequest();
        request.setSignName(smsProperties.getSignName());
        request.setTemplateCode(smsProperties.getTemplateCode());
        request.setPhoneNumbers(mobile);
        //将map转为json格式
        Map<String,String> param = new HashMap<>();
        param.put("code",checkCode);
        Gson gson = new Gson();
        String code = gson.toJson(param);
        //将json格式的code放进里面
        request.setTemplateParam(code);
        SendSmsResponse response = client.getAcsResponse(request);
        //配置参考：短信服务 -》 系统设置 -》 国内消息设置
        //控制所有短信流向限流 （同一手机号：一分钟一条，一个小时五条，一天十条）
        String message = response.getMessage();
        if("isv.BUSINESS_LIMIT_CONTROL".equals(response.getCode())){
            log.error("短信发送过于频繁:"+"code-"+response.getCode()+",message-"+message);
            throw new GuliException(ResultCodeEnum.SMS_SEND_ERROR_BUSINESS_LIMIT_CONTROL);
        }

        //解析响应结果
        if(message.equalsIgnoreCase("OK")){
            log.error("短信发送失败:"+"code-"+response.getCode()+",message-"+message);
            throw new GuliException(ResultCodeEnum.SMS_SEND_ERROR);
        }
    }
}
