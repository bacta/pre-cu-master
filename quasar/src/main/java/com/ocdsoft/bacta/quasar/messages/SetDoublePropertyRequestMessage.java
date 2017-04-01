package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class SetDoublePropertyRequestMessage extends RequestMessage<SetDoublePropertyRequestMessage> {
    private final String propertyName;
    private final double value;

    public SetDoublePropertyRequestMessage(ActorRef<?> from, String propertyName, double value) {
        super(from);
        this.propertyName = propertyName;
        this.value = value;
    }
}
