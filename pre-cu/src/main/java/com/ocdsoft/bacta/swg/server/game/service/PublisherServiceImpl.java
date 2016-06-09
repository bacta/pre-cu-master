package com.ocdsoft.bacta.swg.server.game.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.service.PublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Created by kyle on 5/21/2016.
 */
@Singleton
public class PublisherServiceImpl implements PublisherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublisherServiceImpl.class);

    private final Multimap<Class, Consumer> eventConsumers;

    @Inject
    public PublisherServiceImpl() {
        eventConsumers = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    }

    @Override
    public final <T extends Event> boolean subscribe(Class<T> eventClass, Consumer<T> handleMethod) {
        return eventConsumers.put(eventClass, handleMethod);
    }

    @Override
    public final <T extends Event> boolean unsubscribe(Class<T> eventClass, Consumer<T> handleMethod) {
        return eventConsumers.remove(eventClass, handleMethod);
    }

    @Override
    public final <T extends Event> void triggerEvent(final T event) {
        final Collection<Consumer> consumers = eventConsumers.get(event.getClass());
        if (consumers != null) {
            for (Consumer consumer : consumers) {
                consumer.accept(event);
            }
        }
    }
}
