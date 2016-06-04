package com.ocdsoft.bacta.swg.server.chat.service;

import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.io.udp.PublisherService;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Subscribable;

import java.util.function.Consumer;

/**
 * Created by crush on 5/27/2016.
 */
@Singleton
public class ChatPublisherService implements PublisherService {

    @Override
    public <T extends GameNetworkMessage & Subscribable> void messageSubscribe(SoeUdpConnection connection, Class<T> messageClass) {

    }

    @Override
    public <T extends GameNetworkMessage & Subscribable> void messageUnsubscribe(SoeUdpConnection connection, Class<T> messageClass) {

    }

    @Override
    public <T extends Event> boolean subscribe(Class<T> eventClass, Consumer<T> handleMethod) {
        return false;
    }

    @Override
    public <T extends Event> boolean unsubscribe(Class<T> eventClass, Consumer<T> handleMethod) {
        return false;
    }

    @Override
    public void onEvent(Event event) {

    }
}
