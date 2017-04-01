package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.behaviors.ResponseMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class GetShortPropertyResponseMessage extends ResponseMessage {
    private final short value;

    public GetShortPropertyResponseMessage(Object id, short value) {
        super(id);
        this.value = value;
    }
}
