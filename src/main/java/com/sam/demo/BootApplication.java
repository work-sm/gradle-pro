package com.sam.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ComponentScan("com.sam.demo")
@EnableDiscoveryClient
@ConditionalOnClass(SpringfoxWebMvcConfiguration.class)
public class BootApplication implements WebMvcConfigurer {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BootApplication.class, args);
        while(true){
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            String username = environment.getProperty("test.name");
            String age = environment.getProperty("test.age");
            System.out.println("username:"+username+" | age:"+age);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
