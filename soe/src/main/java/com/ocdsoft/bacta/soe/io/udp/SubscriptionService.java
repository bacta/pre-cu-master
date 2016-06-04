package com.ocdsoft.bacta.soe.io.udp;

import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.event.Subscriber;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Subscribable;

/**
 * Created by kyle on 5/21/2016.
 */
public interface SubscriptionService {
    <T extends Event> boolean subscribe(final Class<T> event, final Subscriber subscriber);
    <T extends Event> boolean unsubscribe(final Class<T> event, final Subscriber subscriber);
    <T extends GameNetworkMessage & Subscribable> void messageSubscribe(final SoeUdpConnection connection, final Class<T> messageClass);
    <T extends GameNetworkMessage & Subscribable> void messageUnsubscribe(final SoeUdpConnection connection, final Class<T> messageClass);
    void onEvent(Event event);
}
