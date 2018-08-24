package com.example.demo.config;

import com.example.demo.dto.Person;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheney on 2018/8/24.
 */
@Component
@ConfigurationProperties("com")
@Data
public class ComConfig {

    private List<Person> people = new ArrayList<>();
}
