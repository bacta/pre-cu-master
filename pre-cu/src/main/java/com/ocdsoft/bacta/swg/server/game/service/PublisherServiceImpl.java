package com.ocdsoft.bacta.swg.server.game.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.service.PublisherService;
import com.ocdsoft.bacta.swg.server.game.script.ScriptService;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by kyle on 5/21/2016.
 */
@Singleton
public class PublisherServiceImpl implements PublisherService {

    private final Multimap<Class, Consumer> eventConsumers;
    private final ScriptService scriptService;

    @Inject
    public PublisherServiceImpl(final ScriptService scriptService) {
        eventConsumers = Multimaps.synchronizedSetMultimap(HashMultimap.create());
        this.scriptService = scriptService;
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
        if(consumers != null) {
            for(Consumer consumer : consumers) {
                consumer.accept(event);
            }
        }
        //scriptService.triggerScripts(event);
    }
}
