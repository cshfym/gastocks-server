package com.gastocks.server.jms.models

/**
 * An instance of a generic message with its identifier and accompanying service class to execute.
 * Messages queued with this object will invoke the serviceClass.processWithIdentifier method when dequeued.
 * See also: {@link IGenericServiceInvoker}
 */
class GenericIdServiceMessage {

    String identifier

    Class serviceClass

}
