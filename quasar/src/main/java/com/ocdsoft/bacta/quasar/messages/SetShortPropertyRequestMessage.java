package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class SetShortPropertyRequestMessage extends RequestMessage<SetShortPropertyRequestMessage> {
    private final String propertyName;
    private final short value;

    public SetShortPropertyRequestMessage(ActorRef<?> from, String propertyName, short value) {
        super(from);
        this.propertyName = propertyName;
        this.value = value;
    }
}
