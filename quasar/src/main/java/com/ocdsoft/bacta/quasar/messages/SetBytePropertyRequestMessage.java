package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class SetBytePropertyRequestMessage extends RequestMessage<SetBytePropertyRequestMessage> {
    private final String propertyName;
    private final byte value;

    public SetBytePropertyRequestMessage(ActorRef<?> from, String propertyName, byte value) {
        super(from);
        this.propertyName = propertyName;
        this.value = value;
    }
}
