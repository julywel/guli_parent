package com.atguigu.guli.service.ucenter.controller.api;


import com.alibaba.nacos.client.utils.StringUtils;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.common.base.util.HttpClientUtils;
import com.atguigu.guli.common.base.util.JwtInfo;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.service.MemberService;
import com.atguigu.guli.service.ucenter.util.UcenterProperties;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//@CrossOrigin
@Api(tags = "连接微信api")
@Controller
@RequestMapping("/api/ucenter/wx")
@Slf4j
public class ApiWxController {

    @Autowired
    private UcenterProperties ucenterProperties;

    @Autowired
    private MemberService memberService;

    @GetMapping("login")
    public String genQrConnect(HttpSession session) {
        //组装url ： https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=回调地址&response_type=code&scope=snsapi_login&state=3d6be0a4035d839573b04816624a415e#wechat_redirect
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect?" +
                "appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s#wechat_redirect";
        String redirectUri = "";
        try {
            redirectUri = URLEncoder.encode(ucenterProperties.getRedirectUri(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.URL_ENCODE_ERROR);
        }

        //生成随机数state 防止csrf攻击
        String state = UUID.randomUUID().toString();
        //将state存入session
        session.setAttribute("wx_open_state",state);

        String grcodeUrl = String.format(baseUrl,
                ucenterProperties.getAppId(),
                redirectUri,
                state);
        // 跳转到组装的url地址中去
        return "redirect:" + grcodeUrl;
    }

    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session){
//        System.out.println("callback被调用");
//        System.out.println("code:" + code);
//        System.out.println("state:" + state);

        if( StringUtils.isEmpty(code) || StringUtils.isEmpty(state)){
            log.error("非法回调请求");
            throw new GuliException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        String sessionState = (String) session.getAttribute("wx_open_state");
        if(!state.equals(sessionState)) {
            log.error("非法回调请求");
            throw new GuliException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        /*携带code临时票据，和appid以及appsecret请求access_token和openid(微信唯一标识)*/
        String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
        //组装参数
        Map<String,String> accessTokenParam = new HashMap<>();
        accessTokenParam.put("appid", ucenterProperties.getAppId());
        accessTokenParam.put("secret", ucenterProperties.getAppSecret());
        accessTokenParam.put("code",code);
        accessTokenParam.put("grant_type","authorization_code");
        HttpClientUtils client = new HttpClientUtils(accessTokenUrl, accessTokenParam);

        String result = "";
        try {
            /*帮助我们发送请求使用:组装完整的url字符串，发送请求*/
            client.get();
            //得到响应
            result = client.getContent();
            //System.out.println("result =" + result);
        } catch (Exception e) {
            log.error("获取access_token失败");
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        Gson gson = new Gson();
        //将字符串转换成hashmap
        HashMap<String,Object> resultMap = gson.fromJson(result, HashMap.class);
        Object errcodeObj = resultMap.get("errcode");
        if(errcodeObj != null){
            Double errcode = (Double) resultMap.get("errcode");
            String errmsg = (String) resultMap.get("errmsg");
            log.error("获取access_token失败:" + "code: " + errcode + ",message:" + errmsg);
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        //解析出结果中的access_token和openid
        String accessToken = (String) resultMap.get("access_token");
        String openid = (String) resultMap.get("openid");
//        System.out.println("access_token:" + accessToken);
//        System.out.println("openid:"+openid);


        /*获得用户个人信息*/
        //在本地数据库中查找当前微信用户的信息
        Member member = memberService.getByOpenId(openid);


        if(member == null) {
            //if: 如果当前用户不存在，则去微信的资源服务器获取用户个人信息（access_token）
            //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo";
            //组装参数
            Map<String,String> baseUserInfoParam = new HashMap<>();
            baseUserInfoParam.put("access_token",accessToken);
            baseUserInfoParam.put("openid",openid);
            client = new HttpClientUtils(baseUserInfoUrl, baseUserInfoParam);
            String resultUserInfo = "";
            try {
                client.get();
                resultUserInfo = client.getContent();
            } catch (Exception e) {
                log.error(ExceptionUtils.getMessage(e));
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            HashMap<String,Object> resultUserInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
            errcodeObj = resultUserInfoMap.get("errcode");
            if (errcodeObj != null) {
                Double errcode = (Double) resultMap.get("errcode");
                String errmsg = (String) resultMap.get("errmsg");
                log.error("获取用户信息失败:" + "code: " + errcode + ",message:" + errmsg);
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            // 解析出结果中的用户个人信息
            String nickname = (String) resultUserInfoMap.get("nickname");
            String avatar = (String) resultUserInfoMap.get("headimgurl");
            Double sex = (Double) resultUserInfoMap.get("sex");

            //在本地数据库中插入当前微信用户的信息(使用微信账号在本地服务器注册新用户)
            member = new Member();
            member.setOpenid(openid)
                    .setNickname(nickname)
                    .setAvatar(avatar)
                    .setSex(sex.intValue());
            memberService.save(member);
        }
        //if:如果当前用户已存在，则直接使用当前用户的信息登录(生成jwt)
        //member => JWT
        JwtInfo jwtInfo = new JwtInfo();
        //您使用mybatisplus时候，你插入一个对象，他的插入进去后的全部信息会返回给该对象
        //比如在插入时使用的雪花算法id插入后会进行返回到该对象
        jwtInfo.setId(member.getId());
        jwtInfo.setAvatar(member.getAvatar());
        jwtInfo.setNickname(member.getNickname());
        String token = JwtUtils.getJwtToken(jwtInfo, 24*60*60);


        return "redirect:http://localhost:3333?token="+token;

    }
}
