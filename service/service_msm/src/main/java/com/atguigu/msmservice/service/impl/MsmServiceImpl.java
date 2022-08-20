package com.atguigu.msmservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.msmservice.service.MsmService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {
    @Override
    public boolean send(Map<String, Object> param, String phone) {

        if(StringUtils.isEmpty(phone)) return false;

        DefaultProfile profile =
                DefaultProfile.getProfile("default", "LTAI5t6Ck9eiAmZK4yri6Rg6", "NYFxh1D4BvsGjL8sqhRe2YKYpS9w2F");
        IAcsClient client = new DefaultAcsClient(profile);


        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");//请求阿里云哪里，默认不能改
        request.setVersion("2017-05-25");
        request.setAction("SendSms");//请求哪个方法



        request.putQueryParameter("PhoneNumbers",phone);//设置要发送的【手机号】
        request.putQueryParameter("SignName","阿里云短信测试");//申请阿里云短信服务的【签名名称】
        request.putQueryParameter("TemplateCode","SMS_154950909");//申请阿里云短信服务的【模版中的 模版CODE】

        //要求传递的code验证码为jason格式，可以使用JSONObject.toJSONString()将map转为json格式
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));


        //最终发送
        try {
            CommonResponse response = client.getCommonResponse(request);
            return response.getHttpResponse().isSuccess();
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }

    }
}
