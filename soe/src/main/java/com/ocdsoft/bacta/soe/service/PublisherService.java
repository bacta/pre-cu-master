package com.ocdsoft.bacta.soe.service;

import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Subscribable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by kyle on 5/21/2016.
 */
@Singleton
public class PublisherService {

    private final Map<Class, Set<Consumer>> eventConsumers;

    public PublisherService() {
        eventConsumers = Collections.synchronizedMap(new HashMap<>());
    }

    public final <T extends Event> boolean subscribe(Class<T> eventClass, Consumer<T> handleMethod) {

        Set<Consumer> consumers = eventConsumers.get(eventClass);
        if(consumers == null) {
            consumers = Collections.synchronizedSet(new HashSet<>());
            eventConsumers.put(eventClass, consumers);
        }

        return consumers.add(handleMethod);
    }

    public final <T extends Event> boolean unsubscribe(Class<T> eventClass, Consumer<T> handleMethod) {
        Set<Consumer> consumers = eventConsumers.get(eventClass);
        if (consumers == null) {
            return true;
        }

        boolean result = consumers.remove(handleMethod);
        if(consumers.isEmpty()) {
            eventConsumers.remove(eventClass);
        }
        return result;
    }

    public final <T extends Event> void onEvent(final T event) {
        final Set<Consumer> consumers = eventConsumers.get(event.getClass());
        if(consumers != null) {
            for(Consumer consumer : consumers) {
                consumer.accept(event);
            }
        }
    }
}
