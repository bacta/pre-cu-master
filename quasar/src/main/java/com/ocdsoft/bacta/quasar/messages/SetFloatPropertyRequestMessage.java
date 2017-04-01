package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class SetFloatPropertyRequestMessage extends RequestMessage<SetFloatPropertyRequestMessage> {
    private final String propertyName;
    private final float value;

    public SetFloatPropertyRequestMessage(ActorRef<?> from, String propertyName, float value) {
        super(from);
        this.propertyName = propertyName;
        this.value = value;
    }
}
