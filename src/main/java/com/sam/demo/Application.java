package com.sam.demo;

import com.google.common.base.Strings;
import com.sam.demo.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;

@Slf4j
public class Application {

    private static final Object forWait = new Object();

    /**
     * ApplicationContext + [Ctrl + H]
     * 缓存策略:FIFO、FileLFU、LFU、LRU、Timed
     * 令牌桶流量控制
     * Hystrix 熔断机制
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // 获取jvm名
        String vm = ManagementFactory.getRuntimeMXBean().getName();
        if (Strings.isNullOrEmpty(vm)) {
            log.error("can't get pid");
            return;
        }

        File pid = new File("pid");
        if (pid.exists()) {
            log.error("the pid file is exist at {}", pid.getAbsolutePath());
            return;
        }

        // 将jvm进程id保存到pid文件
        try (FileOutputStream out = new FileOutputStream(pid)) {
            out.write(vm.split("@")[0].getBytes());
            out.flush();
        }

        log.info("Cube main starting at {}", vm);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        context.start();
        synchronized (forWait) {
            forWait.wait();
        }
        context.close();
    }

}
