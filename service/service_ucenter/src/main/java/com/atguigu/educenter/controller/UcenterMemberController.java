package com.atguigu.educenter.controller;



import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.ordervo.UcenterMemberOrder;
import com.atguigu.commonutils.vo.UcenterMemberVo;
import com.atguigu.educenter.entity.UcenterMember;
import com.atguigu.educenter.entity.vo.RegisterVo;
import com.atguigu.educenter.service.UcenterMemberService;
import com.google.common.annotations.GwtCompatible;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author sjw
 * @since 2022-07-30
 */
@RestController
@RequestMapping("/educenter/member")
@CrossOrigin
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService memberService;

    //登录
    @PostMapping("/login")
    public R loginUser(@RequestBody UcenterMember member){
        String token = memberService.login(member);
        return R.ok().data("token",token);
    }

    //注册
    @PostMapping("/register")
    public R registerUser(@RequestBody RegisterVo registerVo){
        memberService.register(registerVo);

        return R.ok();
    }

    //根据token获取用户信息
    @GetMapping("/getMemberInfo")
    public R getMemberInfo(HttpServletRequest request){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //根据id查询用户信息
        UcenterMember member = memberService.getById(memberId);
        return R.ok().data("userInfo",member);
    }

    //根据用户id查询用户
    @PostMapping("/getUserInfoOrder/{id}")
    public UcenterMemberOrder getMemberInfoById(@PathVariable String id){
        UcenterMember member = memberService.getById(id);
        UcenterMemberOrder ucenterMemberOrder = new UcenterMemberOrder();
        BeanUtils.copyProperties(member,ucenterMemberOrder);
        return ucenterMemberOrder;
    }

    //查询某一天的注册人数
    @GetMapping("/countRegister/{day}")
    public R countRegister(@PathVariable String day){
        Integer count = memberService.countRegister(day);
        return R.ok().data("countRegister",count);
    }

}

