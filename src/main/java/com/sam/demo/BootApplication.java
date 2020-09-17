package com.sam.demo;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;

@SpringBootConfiguration
@ComponentScan("com.sam.demo")
@EnableAutoConfiguration
@ConditionalOnClass(SpringfoxWebMvcConfiguration.class)
public class BootApplication implements WebMvcConfigurer, CommandLineRunner {

    @NacosInjected
    private NamingService namingService;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void run(String... args) throws Exception {
        // 通过Naming服务注册实例到注册中心
//        namingService.registerInstance(applicationName, "127.0.0.1", serverPort);

        // 根据服务名从注册中心获取一个健康的服务实例
//        Instance instance = namingService.selectOneHealthyInstance(applicationName);
        // 这里只是为了方便才新建RestTemplate实例
//        RestTemplate template = new RestTemplate();
//        String url = String.format("http://%s:%d/hello?name=throwable", instance.getIp(), instance.getPort());
//        String result = template.getForObject(url, String.class);
//        System.out.println(String.format("请求URL:%s,响应结果:%s", url, result));
    }

}
