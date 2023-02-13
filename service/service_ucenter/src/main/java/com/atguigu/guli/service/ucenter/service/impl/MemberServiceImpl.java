package com.atguigu.guli.service.ucenter.service.impl;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.FormUtils;
import com.atguigu.guli.common.base.util.JwtInfo;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.common.base.util.MD5;
import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.entity.vo.LoginVo;
import com.atguigu.guli.service.ucenter.entity.vo.RegisterVo;
import com.atguigu.guli.service.ucenter.mapper.MemberMapper;
import com.atguigu.guli.service.ucenter.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author TJS
 * @since 2022-11-20
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void register(RegisterVo registerVo) {
        //校验参数
        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String code = registerVo.getCode();
        String password = registerVo.getPassword();
        if(StringUtils.isEmpty(mobile) || !FormUtils.isMobile(mobile)){
            throw new GuliException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }
        if(StringUtils.isEmpty(nickname)
                || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(code)){
            throw new GuliException(ResultCodeEnum.PARAM_ERROR);
        }

        //先校验验证码 如果验证码错误，就可以不用走数据库了，否则，怎么都会去访问数据库
        String checkCode = (String) redisTemplate.opsForValue().get(mobile);
        if(!code.equals(checkCode)){
            throw new GuliException(ResultCodeEnum.CODE_ERROR);
        }

        //校验用户是否被注册过
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getMobile,mobile);
        Integer count = baseMapper.selectCount(queryWrapper);
        if(count > 0) {
            throw new GuliException(ResultCodeEnum.REGISTER_MOBLE_ERROR);
        }
        //注册
        Member member = new Member();
        member.setMobile(mobile)
                .setPassword(MD5.encrypt(password))
                .setNickname(nickname)
                .setAvatar("https://guli-file-tjs.oss-cn-hangzhou.aliyuncs.com/avatar/2022/11/09/4dc71f96-3642-413c-9a03-519f0b6919c8.jpg")
                .setDisabled(false);
        baseMapper.insert(member);
    }

    @Override
    public String login(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //校验:参数是否合法
        if(StringUtils.isEmpty(mobile)
                || !FormUtils.isMobile(mobile)
                || StringUtils.isEmpty(password)) {
            throw new GuliException(ResultCodeEnum.PARAM_ERROR);
        }
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Member::getMobile,mobile);
        Member member = baseMapper.selectOne(queryWrapper);
        //校验手机号是否存在
        if(member == null) {
            throw new GuliException(ResultCodeEnum.NO_REGISTER_ERROR);
        }
        //校验密码是否存在
        if(!MD5.encrypt(password).equals(member.getPassword())){
            throw new GuliException(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        //校验用户是否被禁用
        if(member.getDisabled()){
            throw new GuliException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        //登录:生成token
        JwtInfo jwtInfo = new JwtInfo();
        jwtInfo.setId(member.getId());
        jwtInfo.setAvatar(member.getAvatar());
        jwtInfo.setNickname(member.getNickname());

        String jwtToken = JwtUtils.getJwtToken(jwtInfo, 60*60*24);

        return jwtToken;
    }

    @Override
    public Member getByOpenId(String openid) {

        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getOpenid,openid);
        Member member = baseMapper.selectOne(queryWrapper);
        return member;
    }

    @Override
    public MemberDto getMemberDtoByMemberId(String memberId) {
        Member member = baseMapper.selectById(memberId);
        MemberDto memberDto = new MemberDto();
        memberDto.setId(member.getId())
                .setMobile(member.getMobile())
                .setNickname(member.getNickname());
        return memberDto;
    }

    @Override
    public Integer countRegisterNum(String day) {
        return baseMapper.selectRegisterNumByDay(day);
    }
}
