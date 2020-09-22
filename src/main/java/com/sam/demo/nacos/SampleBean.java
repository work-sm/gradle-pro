package com.sam.demo.nacos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Getter
@Setter
@RefreshScope //打开动态刷新功能
public class SampleBean {

    @Value("${user.name}")
    String userName;

    @Value("${user.age}")
    int age;

}
