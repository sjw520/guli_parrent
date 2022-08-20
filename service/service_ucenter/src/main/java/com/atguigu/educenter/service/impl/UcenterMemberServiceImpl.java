package com.atguigu.educenter.service.impl;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.MD5;
import com.atguigu.educenter.entity.UcenterMember;
import com.atguigu.educenter.entity.vo.RegisterVo;
import com.atguigu.educenter.mapper.UcenterMemberMapper;
import com.atguigu.educenter.service.UcenterMemberService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author sjw
 * @since 2022-07-30
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @Override
    public String login(UcenterMember member) {
        //获取手机号和密码
        String mobile = member.getMobile();
        String password = member.getPassword();

        //判断输入的手机号和密码是否为空
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            throw new GuliException(20001,"登录失败");
        }
        //判断手机号是否正确
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        UcenterMember mobileMember = baseMapper.selectOne(wrapper);
        if(mobileMember == null){
            throw new GuliException(20001,"登录失败");
        }
        // MD5加密是不可逆性的，不能解密，只能加密
        //将获取到的密码经过MD5加密与数据库比较
        //判断密码是否正确

        if(!MD5.encrypt(password).equals(mobileMember.getPassword())){
            throw new GuliException(20001,"密码不正确");
        }
        //判断用户是否禁用
        if(mobileMember.getIsDisabled()){
            throw  new GuliException(20001,"用户被禁用");
        }
        //生成jwtToken
        String token = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());

        return token;
    }

    @Override
    public void register(RegisterVo registerVo) {
        //获取前端传来的数据
        String nickname = registerVo.getNickname(); //昵称
        String code = registerVo.getCode(); //验证码
        String mobile = registerVo.getMobile(); //手机号
        String password = registerVo.getPassword(); //密码

        if(StringUtils.isEmpty(nickname) || StringUtils.isEmpty(code) || StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            throw new GuliException(20001,"注册失败");

        }

        //获取redis验证码
        String redisCode = redisTemplate.opsForValue().get(mobile);
        if(!redisCode.equals(code)){
            throw new GuliException(200001,"注册失败");
        }

        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if(count>0){
            throw new GuliException(20001,"手机号重复，注册失败");
        }


        UcenterMember ucenterMember = new UcenterMember();
        ucenterMember.setNickname(nickname);
        ucenterMember.setMobile(mobile);
        ucenterMember.setPassword(MD5.encrypt(password));
        ucenterMember.setIsDisabled(false);//用户不禁用
        ucenterMember.setAvatar("https://online-teach-file.oss-cn-beijing.aliyuncs.com/teacher/2019/10/30/65423f14-49a9-4092-baf5-6d0ef9686a85.png");


        baseMapper.insert(ucenterMember);

    }

    @Override
    public UcenterMember getOpenIdMember(String openid) {

        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openid);
        UcenterMember member = baseMapper.selectOne(wrapper);
        return member;
    }

    @Override
    public Integer countRegister(String day) {

        return baseMapper.countRegister(day);
    }
}
