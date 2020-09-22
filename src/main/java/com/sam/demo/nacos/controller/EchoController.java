package com.sam.demo.nacos.controller;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.sam.demo.nacos.EchoService;
import com.sam.demo.nacos.SampleBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class EchoController {

    @NacosInjected
    private NamingService namingService;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    @Autowired(required = false)
    private SampleBean sampleBean;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EchoService echoService;

    @GetMapping(value = "/echo/{string}")
    public String echo(@PathVariable String string) {
        return string;
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

    //RestTemplate
    @RequestMapping(value = "/echo-rest/{str}", method = RequestMethod.GET)
    public String rest(@PathVariable String str) {
        return restTemplate.getForObject("http://" + applicationName + "/echo/" + str, String.class);
    }

    //FeignClient
    @RequestMapping(value = "/echo-feign/{str}", method = RequestMethod.GET)
    public String feign(@PathVariable String str) {
        return echoService.echo(str);
    }

}
