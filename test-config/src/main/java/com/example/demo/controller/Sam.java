package com.example.demo.controller;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by cheney on 2018/8/24.
 */
@Component
@ConfigurationProperties(prefix = "person.sam")
@Data
public class Sam {

    private String name;
    private int age;
    private String desc;
}
