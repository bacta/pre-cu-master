package com.ocdsoft.bacta.soe.io.udp;

import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Subscribable;

import java.util.function.Consumer;

/**
 * Created by kyle on 5/21/2016.
 */
public interface PublisherService {
    <T extends GameNetworkMessage & Subscribable> void messageSubscribe(final SoeUdpConnection connection, final Class<T> messageClass);
    <T extends GameNetworkMessage & Subscribable> void messageUnsubscribe(final SoeUdpConnection connection, final Class<T> messageClass);
    <T extends Event> void onEvent(final T event);

    <T extends Event> boolean subscribe(Class<T> eventClass, Consumer<T> handleMethod);
    <T extends Event> boolean unsubscribe(Class<T> eventClass, Consumer<T> handleMethod);
}
