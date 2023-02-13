package com.atguigu.guli.service.ucenter.mapper;

import com.atguigu.guli.service.ucenter.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author TJS
 * @since 2022-11-20
 */
@Repository
public interface MemberMapper extends BaseMapper<Member> {

    Integer selectRegisterNumByDay(String day);

}
