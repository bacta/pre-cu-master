package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class SetIntPropertyRequestMessage extends RequestMessage {
    private final String propertyName;
    private final int value;

    public SetIntPropertyRequestMessage(ActorRef<?> from, String propertyName, int value) {
        super(from);
        this.propertyName = propertyName;
        this.value = value;
    }
}
