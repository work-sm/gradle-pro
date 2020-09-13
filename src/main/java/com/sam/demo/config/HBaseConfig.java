package com.sam.demo.config;

import com.sam.demo.hbase.HBaseService;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@EnableConfigurationProperties({HBaseProperties.class})
public class HBaseConfig {

    private static final String HBASE_QUORUM = "hbase.zookeeper.quorum";
    private static final String HBASE_PORT = "hbase.zookeeper.property.clientPort";

    @Bean
    public HBaseService getHbaseService(HBaseProperties hbaseProperties) {
        //设置临时的hadoop环境变量，之后程序会去这个目录下的\bin目录下找winutils.exe工具，windows连接hadoop时会用到
        //System.setProperty("hadoop.home.dir", "D:\\Program Files\\Hadoop");
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set(HBASE_QUORUM, hbaseProperties.getQuorum());
        conf.set(HBASE_PORT, hbaseProperties.getClientPort());
        return new HBaseService(conf);
    }

}
