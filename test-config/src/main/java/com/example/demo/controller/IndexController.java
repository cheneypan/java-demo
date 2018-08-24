package com.example.demo.controller;

import com.example.demo.config.ComConfig;
import com.example.demo.config.PersonConfig;
import com.example.demo.dto.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheney on 2018/8/24.
 */
@RestController
@Slf4j
public class IndexController {

    @Autowired
    private Sam sam;
    @Autowired
    private Person tom;
    @Autowired
    private ComConfig comConfig;
    @Autowired
    private PersonConfig personConfig;

    @Bean(name = "tom")
    @ConfigurationProperties(prefix = "person.tom")
    public Person tom(){
        return new Person();
    }

    @GetMapping
    public Object index(){
        log.info("name = {}", tom.getName());
        return personConfig;
    }
}
