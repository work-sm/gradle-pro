package com.sam.demo.nacos;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("test")
@RestController
public interface EchoService {
    @GetMapping("/echo/{str}")
    String echo(@PathVariable("str") String str);
}
