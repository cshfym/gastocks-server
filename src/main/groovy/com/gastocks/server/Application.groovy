package com.gastocks.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.DispatcherServlet

@EnableScheduling
@SpringBootApplication
class Application {
    static void main(String[] args) {
        ApplicationContext context = SpringApplication.run Application, args
        DispatcherServlet dispatcherServlet = (DispatcherServlet)context.getBean("dispatcherServlet")
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true)
    }
}