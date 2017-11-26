package com.gastocks.server.jms.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.config.JmsListenerContainerFactory
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType

import javax.jms.ConnectionFactory

@Configuration
class JmsConfiguration {

    @Value('${symbol.consumer.queue.concurrency}')
    String SYMBOL_CONSUMER_QUEUE_CONCURRENCY

    @Value('${simulation.consumer.queue.concurrency}')
    String SIMULATION_CONSUMER_QUEUE_CONCURRENCY

    @Value('${symbol.extended.fill.consumer.queue.concurrency}')
    String SYMBOL_EXTENDED_FILL_CONSUMER_QUEUE_CONCURRENCY

    @Value('${quote.price.change.backfill.consumer.queue.concurrency}')
    String QUOTE_PRICE_CHANGE_BACKFILL_CONSUMER_QUEUE_CONCURRENCY

    @Value('${quote.audit.queue.consumer.queue.concurrency}')
    String QUOTE_AUDIT_CONSUMER_QUEUE_CONCURRENCY

    @Value('${quote.audit.reload.consumer.queue.concurrency}')
    String QUOTE_AUDIT_RELOAD_CONSUMER_QUEUE_CONCURRENCY

    @Value('${company.dump.consumer.queue.concurrency}')
    String COMPANY_DUMP_CONSUMER_QUEUE_CONCURRENCY

    @Value('${generic.service.id.message.queue.concurrency}')
    String GENERIC_SERVICE_ID_MESSAGE_QUEUE_CONCURRENCY

    @Value('${sector.quote.backfill.consumer.queue.concurrency}')
    String SECTOR_QUOTE_BACKFILL_CONSUMER_QUEUE_CONCURRENCY

    /**
     * Bean corresponds to the "quoteFactory" JMS listener for consuming symbols and loading quotes.
     * @param connectionFactory
     * @param configurer
     * @return {@JmsListenerContainerFactory}
     */
    @Bean
    JmsListenerContainerFactory<?> quoteFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(SYMBOL_CONSUMER_QUEUE_CONCURRENCY)
        configurer.configure(factory, connectionFactory)
        factory
    }

    /**
     * Bean corresponds to the "simulationFactory" JMS listener for consuming simulation requests.
     * @param connectionFactory
     * @param configurer
     * @return {@JmsListenerContainerFactory}
     */
    @Bean
    JmsListenerContainerFactory<?> simulationFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(SIMULATION_CONSUMER_QUEUE_CONCURRENCY)
        configurer.configure(factory, connectionFactory)
        factory
    }

    /**
     * Bean corresponds to the "extendedSymbolFillFactory" JMS listener for consuming extended symbol backfill requests.
     * @param connectionFactory
     * @param configurer
     * @return {@JmsListenerContainerFactory}
     */
    @Bean
    JmsListenerContainerFactory<?> extendedSymbolFillFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(SYMBOL_EXTENDED_FILL_CONSUMER_QUEUE_CONCURRENCY)
        configurer.configure(factory, connectionFactory)
        factory
    }

    /**
     * Bean corresponds to the "quotePriceChangeFillFactory" JMS listener for consuming quote price change backfill requests.
     * @param connectionFactory
     * @param configurer
     * @return {@JmsListenerContainerFactory}
     */
    @Bean
    JmsListenerContainerFactory<?> quotePriceChangeFillFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(QUOTE_PRICE_CHANGE_BACKFILL_CONSUMER_QUEUE_CONCURRENCY)
        configurer.configure(factory, connectionFactory)
        factory
    }

    /**
     * Bean corresponds to the "quoteAuditFactory" JMS listener for consuming quote audit requests.
     * @param connectionFactory
     * @param configurer
     * @return {@JmsListenerContainerFactory}
     */
    @Bean
    JmsListenerContainerFactory<?> quoteAuditFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(QUOTE_AUDIT_CONSUMER_QUEUE_CONCURRENCY)
        configurer.configure(factory, connectionFactory)
        factory
    }

    /**
     * Bean corresponds to the "quoteAuditReloadFactory" JMS listener for consuming quote audit correction requests.
     * @param connectionFactory
     * @param configurer
     * @return {@JmsListenerContainerFactory}
     */
    @Bean
    JmsListenerContainerFactory<?> quoteAuditReloadFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(QUOTE_AUDIT_RELOAD_CONSUMER_QUEUE_CONCURRENCY)
        configurer.configure(factory, connectionFactory)
        factory
    }

    /**
     * Bean corresponds to the "intrinioCompanyDumpFactory" JMS listener for consuming quote audit correction requests.
     * @param connectionFactory
     * @param configurer
     * @return {@JmsListenerContainerFactory}
     */
    @Bean
    JmsListenerContainerFactory<?> intrinioCompanyDumpFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(COMPANY_DUMP_CONSUMER_QUEUE_CONCURRENCY)
        configurer.configure(factory, connectionFactory)
        factory
    }

    /**
     * Bean corresponds to the "genericServiceMessageFactory" JMS listener for consuming symbols and loading quotes.
     * @param connectionFactory
     * @param configurer
     * @return {@JmsListenerContainerFactory}
     */
    @Bean
    JmsListenerContainerFactory<?> genericServiceMessageFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(GENERIC_SERVICE_ID_MESSAGE_QUEUE_CONCURRENCY)
        configurer.configure(factory, connectionFactory)
        factory
    }

    /**
     * Bean corresponds to the "genericServiceMessageFactory" JMS listener for consuming symbols and loading quotes.
     * @param connectionFactory
     * @param configurer
     * @return {@JmsListenerContainerFactory}
     */
    @Bean
    JmsListenerContainerFactory<?> sectorQuoteBackfillMessageFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        def factory = new DefaultJmsListenerContainerFactory()
        factory.setConcurrency(SECTOR_QUOTE_BACKFILL_CONSUMER_QUEUE_CONCURRENCY)
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
