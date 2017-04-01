package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class SetObjectPropertyRequestMessage<T> extends RequestMessage<SetObjectPropertyRequestMessage<T>> {
    private final String propertyName;
    private final T value;

    public SetObjectPropertyRequestMessage(ActorRef<?> from, String propertyName, T value) {
        super(from);
        this.propertyName = propertyName;
        this.value = value;
    }
}
