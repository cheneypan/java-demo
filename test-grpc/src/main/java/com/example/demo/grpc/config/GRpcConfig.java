package com.example.demo.grpc.config;

import com.example.demo.grpc.hello.GRpcChannelPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by cheney on 2018/7/17.
 */
@Configuration
public class GRpcConfig {

    @Bean
    @ConfigurationProperties(prefix = "grpc.pool")
    public GenericObjectPoolConfig poolConfig(){
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        return poolConfig;
    }

    @Bean(name = "helloChannelPool", destroyMethod = "close")
    public GRpcChannelPool helloChannelPool(GenericObjectPoolConfig poolConfig,
                                         @Value("${grpc.service.hello.host}") String host,
                                         @Value("${grpc.service.hello.port}") int port) {
        System.out.println("host = " + host + ", port = " + port);
        return new GRpcChannelPool(poolConfig, host, port);
    }
}
