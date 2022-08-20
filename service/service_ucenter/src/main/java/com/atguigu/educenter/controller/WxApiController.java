package com.atguigu.educenter.controller;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.educenter.entity.UcenterMember;
import com.atguigu.educenter.service.UcenterMemberService;
import com.atguigu.educenter.utils.ConstantWxUtils;
import com.atguigu.educenter.utils.HttpClientUtils;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

@CrossOrigin
@Controller
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private UcenterMemberService memberService;

    //获取扫描人信息，添加数据
    @GetMapping("/callback")
    public String callback(String code,String state){


        try {
            //获取code值，临时票据，类似于验证码
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token"+
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";

            //拼接三个参数：id 秘钥 和 code值 通过 code 获取access_token
            String accessTokenUrl = String.format(baseAccessTokenUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET,
                    code);
            String accessToken = HttpClientUtils.get(accessTokenUrl);
            System.out.println(accessToken);

            //从accessTokenInfo中获取出  access_token 和 openid 的值
            //将 accessTokenInfo 转换成 map集合，根据map的key 就可以获取对应的value
            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessToken, HashMap.class);
            String access_token = (String) mapAccessToken.get("access_token");
            String openid = (String) mapAccessToken.get("openid");





            //扫描人信息添加到数据库
            UcenterMember member =  memberService.getOpenIdMember(openid);
            if(member == null){
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";

                String userInfoUrl = String.format(baseUserInfoUrl,
                        access_token,
                        openid);

                String userInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println(userInfo);

                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                String nickname = (String)userInfoMap.get("nickname");
                String headimgurl = (String)userInfoMap.get("headimgurl");
                member = new UcenterMember();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headimgurl);
                memberService.save(member);
            }

            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());

            return "redirect:http://localhost:3000?token="+jwtToken;
        } catch (Exception e) {

            throw new GuliException(20001,"登陆失败");
        }




    }

    //生成微信扫描二维码
    @GetMapping("/login")
    public String getWxCode(){
        //固定地址后面拼接参数
//        String url = " https://open.weixin.qq.com/connect/qrconnect?appid="+
//                ConstantWxUtils.WX_OPEN_APP_ID+"";

        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        //对redirect_url进行URLEncode编码
        String redirect_url = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        try {
            redirect_url = URLEncoder.encode(redirect_url,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = String.format(
                baseUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                redirect_url,
                "atguigu"
        );


        return "redirect:"+url;
    }

}
