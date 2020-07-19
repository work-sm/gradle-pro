package com.sam.demo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/application.properties")
@Import(value = {NettyConfig.class})
@ComponentScan(basePackages = {"com.sam.demo"})
public class AppConfig {
}
