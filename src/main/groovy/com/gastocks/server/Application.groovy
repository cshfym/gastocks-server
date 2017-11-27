package com.gastocks.server

import com.gastocks.server.config.CacheConfiguration
import com.gastocks.server.config.TaskExecutorConfiguration
import com.gastocks.server.jms.config.JmsConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.jms.annotation.EnableJms
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.DispatcherServlet


@EnableJms
@EnableScheduling
@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class)
@PropertySources([
    @PropertySource(value = "classpath:application.properties"),
    @PropertySource(value = "file:/usr/local/conf/gastocks-server/application.properties", ignoreResourceNotFound = true)
])
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
@Import([
    JmsConfiguration.class,
    CacheConfiguration.class,
    TaskExecutorConfiguration.class
])
class Application extends SpringBootServletInitializer {

    static void main(String[] args) {
        ApplicationContext context = SpringApplication.run Application, args
        DispatcherServlet dispatcherServlet = (DispatcherServlet)context.getBean("dispatcherServlet")
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true)
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.sources(Application.class)
    }

}