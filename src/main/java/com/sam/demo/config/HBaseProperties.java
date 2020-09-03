package com.sam.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix="hlht.hbase")
public class HBaseProperties {
    private String quorum;
    private String clientPort;
    private String tablePrefix;
}
