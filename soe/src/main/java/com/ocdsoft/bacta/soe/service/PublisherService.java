package com.ocdsoft.bacta.soe.service;

import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Subscribable;

import java.util.*;

import clojure.lang.IFn;

/**
 * Created by kyle on 5/21/2016.
 */
@Singleton
public class PublisherService {

    private final Map<Class, Set<IFn>> eventCallbacks;

    public PublisherService() {
        eventCallbacks = Collections.synchronizedMap(new HashMap<>());
    }

    public final <T extends Event> boolean subscribe(Class<T> eventClass, IFn handleMethod) {

        Set<IFn> callbacks = eventCallbacks.get(eventClass);
        if(callbacks == null) {
            callbacks = Collections.synchronizedSet(new HashSet<>());
            eventCallbacks.put(eventClass, callbacks);
        }

        return callbacks.add(handleMethod);
    }

    public final <T extends Event> boolean unsubscribe(Class<T> eventClass, IFn handleMethod) {
        Set<IFn> callbacks = eventCallbacks.get(eventClass);
        if (callbacks == null) {
            return true;
        }

        boolean result = callbacks.remove(handleMethod);
        if(callbacks.isEmpty()) {
            eventCallbacks.remove(eventClass);
        }
        return result;
    }

    public final <T extends Event> void onEvent(final T event) {
        final Set<IFn> callbacks = eventCallbacks.get(event.getClass());
        if(callbacks != null) {
            for(IFn f : callbacks) {
                f.invoke(event);
            }
        }
    }
}
