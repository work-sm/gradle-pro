package com.sam.demo.nacos.controller;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.sam.demo.nacos.EchoService;
import com.sam.demo.nacos.SampleBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class EchoController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    @Autowired
    private SampleBean sampleBean;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired(required = false)
    private EchoService echoService;

    @NacosInjected
    private NamingService namingService;

    @NacosInjected
    private ConfigService configService;

    @GetMapping("/echo/{string}")
    public String echo(@PathVariable String string) {
        return sampleBean.getUserName() + sampleBean.getAge() + string;
    }

    @GetMapping("/ribbon/{str}")
    public String ribbon(@PathVariable String str) {
        return restTemplate.getForObject("http://" + applicationName + "/echo/" + str, String.class);
    }

    @GetMapping("/rest/{str}")
    public String rest(@PathVariable String str) {
        ServiceInstance serviceInstance = loadBalancerClient.choose(applicationName);
        String url = String.format("http://%s:%s/echo/%s",
                serviceInstance.getHost(), serviceInstance.getPort(), str);
        System.out.println("url -> " + url);
        return restTemplate.getForObject(url, String.class);
    }

    @GetMapping("/feign/{str}")
    public String feign(@PathVariable String str) {
        return echoService.echo(str);
    }

    @GetMapping(value = "/test")
    public String test() {
        try {
            // 通过Naming服务注册实例到注册中心
            namingService.registerInstance(applicationName, "127.0.0.1", serverPort);

            // 根据服务名从注册中心获取一个健康的服务实例
            Instance instance = namingService.selectOneHealthyInstance(applicationName);
            // 这里只是为了方便才新建RestTemplate实例
            String url = String.format("http://%s:%d/echo/hello", instance.getIp(), instance.getPort());
            String result = restTemplate.getForObject(url, String.class);
            return String.format("请求URL:%s,响应结果:%s", url, result);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
