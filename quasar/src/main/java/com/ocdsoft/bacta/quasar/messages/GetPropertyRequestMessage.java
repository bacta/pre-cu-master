package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.behaviors.RequestMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class GetPropertyRequestMessage extends RequestMessage<GetPropertyRequestMessage> {
    private final String propertyName;

    public GetPropertyRequestMessage(ActorRef<?> from, String propertyName) {
        super(from);
        this.propertyName = propertyName;
    }
}
