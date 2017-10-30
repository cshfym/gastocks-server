package com.gastocks.server.jms.models

/**
 * Implementable generic service invoker. Designed to work in-conjunction with a JMS queue receiver, and
 * specific service which calls the "process" method to perform some action on the specified ID.
 */
interface IGenericServiceInvoker {

    void processWithIdentifier(String identifier)
}
