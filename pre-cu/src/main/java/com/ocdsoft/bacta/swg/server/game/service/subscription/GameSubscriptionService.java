package com.ocdsoft.bacta.swg.server.game.service.subscription;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.event.Subscriber;
import com.ocdsoft.bacta.soe.io.udp.SubscriptionService;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Subscribable;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.*;

/**
 * Created by kyle on 5/26/2016.
 */
@Singleton
public class GameSubscriptionService implements SubscriptionService {

    private final Map<Class, Set<Subscriber>> eventSubscribers;

    public GameSubscriptionService() {
        eventSubscribers = Collections.synchronizedMap(new HashMap<>());
    }

    public <T extends Event> boolean subscribe(final Class<T> event, final Subscriber subscriber) {
        Set<Subscriber> subscribers = eventSubscribers.get(event);
        if(subscribers == null) {
            subscribers = Collections.synchronizedSet(new HashSet<>());
            eventSubscribers.put(event, subscribers);
        }

        return subscribers.add(subscriber);
    }

    public <T extends Event> boolean unsubscribe(final Class<T> event, final Subscriber subscriber) {
        Set<Subscriber> subscribers = eventSubscribers.get(event);
        return subscribers == null || subscribers.remove(subscriber);
    }


    @Override
    public void onEvent(Event event) {

    }

    @Override
    public <T extends GameNetworkMessage & Subscribable> void messageSubscribe(final SoeUdpConnection connection, Class<T> messageClass) {

    }

    @Override
    public <T extends GameNetworkMessage & Subscribable> void messageUnsubscribe(final SoeUdpConnection connection, Class<T> messageClass) {

    }


}
