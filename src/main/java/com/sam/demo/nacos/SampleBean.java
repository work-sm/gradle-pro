package com.sam.demo.nacos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@RefreshScope //打开动态刷新功能
@Configuration
public class SampleBean {

    @Value("${test.name}")
    private String userName;

    @Value("${test.age}")
    private String age;

}
