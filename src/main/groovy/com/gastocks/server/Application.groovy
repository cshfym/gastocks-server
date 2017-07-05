package com.gastocks.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class Application {
    static void main(String[] args) {
        SpringApplication.run Application, args
    }
}