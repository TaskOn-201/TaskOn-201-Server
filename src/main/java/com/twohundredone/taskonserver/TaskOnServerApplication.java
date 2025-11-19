package com.twohundredone.taskonserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories
@SpringBootApplication
public class TaskOnServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskOnServerApplication.class, args);
    }

}
