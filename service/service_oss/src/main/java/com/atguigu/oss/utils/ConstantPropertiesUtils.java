package com.atguigu.oss.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

//当项目启动，spring接口，spring加载之后，执行一个接口的方法
@Component
@PropertySource("classpath:application.properties")
public class ConstantPropertiesUtils implements InitializingBean {

    @Value("${aliyun.oss.file.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.file.keyid}")
    private String keyid;

    @Value("${aliyun.oss.file.keysecret}")
    private String keysecret;

    @Value("${aliyun.oss.file.bucketname}")
    private String bucketname;


    public static String END_POINT;
    public static String KEY_ID;
    public static String KEY_SECRET;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        KEY_ID=this.keyid;
        KEY_SECRET=this.keysecret;
        END_POINT=this.endpoint;
        BUCKET_NAME=this.bucketname;
    }
}
