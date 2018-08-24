package com.example.demo.config;

import com.example.demo.dto.Person;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cheney on 2018/8/24.
 */
@Component
@ConfigurationProperties
@Data
public class PersonConfig {

    private Map<String, Person> person = new HashMap<>();
}
