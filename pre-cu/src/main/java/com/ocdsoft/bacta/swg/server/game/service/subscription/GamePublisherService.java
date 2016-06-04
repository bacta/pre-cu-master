package com.ocdsoft.bacta.swg.server.game.service.subscription;

import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.io.udp.PublisherService;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Subscribable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by kyle on 5/26/2016.
 */
@Singleton
public class GamePublisherService implements PublisherService {

    private final Map<Class, Set<Consumer>> eventConsumers;

    public GamePublisherService() {
        eventConsumers = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public <T extends Event> boolean subscribe(Class<T> eventClass, Consumer<T> handleMethod) {

        Set<Consumer> consumers = eventConsumers.get(eventClass);
        if(consumers == null) {
            consumers = Collections.synchronizedSet(new HashSet<>());
            eventConsumers.put(eventClass, consumers);
        }

        return consumers.add(handleMethod);
    }

    @Override
    public <T extends Event> boolean unsubscribe(Class<T> eventClass, Consumer<T> handleMethod) {
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

    @Override
    public <T extends Event> void onEvent(final T event) {
        final Set<Consumer> consumers = eventConsumers.get(event.getClass());
        if(consumers != null) {
            for(Consumer consumer : consumers) {
                consumer.accept(event);
            }
        }
    }

    @Override
    public <T extends GameNetworkMessage & Subscribable> void messageSubscribe(final SoeUdpConnection connection, Class<T> messageClass) {

    }

    @Override
    public <T extends GameNetworkMessage & Subscribable> void messageUnsubscribe(final SoeUdpConnection connection, Class<T> messageClass) {

    }
}
