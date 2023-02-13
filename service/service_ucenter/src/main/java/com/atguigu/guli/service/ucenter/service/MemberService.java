package com.atguigu.guli.service.ucenter.service;

import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.entity.vo.LoginVo;
import com.atguigu.guli.service.ucenter.entity.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author TJS
 * @since 2022-11-20
 */
public interface MemberService extends IService<Member> {

    void register(RegisterVo registerVo);

    String login(LoginVo loginVo);

    //查询member信息
    Member getByOpenId(String openid);

    MemberDto getMemberDtoByMemberId(String memberId);

    //查询该天数有多少注册人
    Integer countRegisterNum(String day);
}
