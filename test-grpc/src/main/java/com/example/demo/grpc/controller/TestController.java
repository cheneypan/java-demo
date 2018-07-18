package com.example.demo.grpc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cheney on 2018/7/17.
 */
@RestController
public class TestController {

    @GetMapping("/")
    public String helloworld() {
        return "helloworld";
    }
}
