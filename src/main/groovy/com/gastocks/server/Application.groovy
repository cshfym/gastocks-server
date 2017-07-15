package com.gastocks.server

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.config.JmsListenerContainerFactory
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.DispatcherServlet

import javax.jms.ConnectionFactory

@EnableJms
@EnableScheduling
@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class)
@PropertySources([
    @PropertySource(value = "classpath:application.properties"),
    @PropertySource(value = "file:/usr/local/conf/gastocks-server/application.properties", ignoreResourceNotFound = true)
])
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
class Application extends SpringBootServletInitializer {

    @Value('${queue.concurrency}')
    String QUEUE_CONCURRENCY

    static void main(String[] args) {
        ApplicationContext context = SpringApplication.run Application, args
        DispatcherServlet dispatcherServlet = (DispatcherServlet)context.getBean("dispatcherServlet")
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true)
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class)
    }

    @Bean
    JmsListenerContainerFactory<?> quoteFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        
        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(QUEUE_CONCURRENCY)
        configurer.configure(factory, connectionFactory)
        factory
    }

    @Bean // Serialize message content to json using TextMessage
    MessageConverter jacksonJmsMessageConverter() {
        def converter = new MappingJackson2MessageConverter()
        converter.with {
            targetType = MessageType.TEXT
            typeIdPropertyName = "_type"
        }
        converter
    }
}