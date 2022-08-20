package com.atguigu.educenter.service;

import com.atguigu.educenter.entity.UcenterMember;
import com.atguigu.educenter.entity.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author sjw
 * @since 2022-07-30
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    //登录
    String login(UcenterMember member);

    //注册
    void register(RegisterVo registerVo);

    UcenterMember getOpenIdMember(String openid);

    Integer countRegister(String day);
}
