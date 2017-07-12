package com.gastocks.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
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
@SpringBootApplication
class Application {

    static void main(String[] args) {
        ApplicationContext context = SpringApplication.run Application, args
        DispatcherServlet dispatcherServlet = (DispatcherServlet)context.getBean("dispatcherServlet")
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true)
    }

    @Bean
    JmsListenerContainerFactory<?> quoteFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        
        def factory = new DefaultJmsListenerContainerFactory()
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