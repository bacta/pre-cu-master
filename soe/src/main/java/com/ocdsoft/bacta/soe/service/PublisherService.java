package com.ocdsoft.bacta.soe.service;

import com.ocdsoft.bacta.soe.event.Event;

import java.util.function.Consumer;

/**
 * Created by kburkhardt on 6/8/16.
 */
public interface PublisherService {
    <T extends Event> boolean subscribe(Class<T> eventClass, Consumer<T> handleMethod);

    <T extends Event> boolean unsubscribe(Class<T> eventClass, Consumer<T> handleMethod);

    <T extends Event> void triggerEvent(T event);
}
