package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.behaviors.ResponseMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class GetBytePropertyResponseMessage extends ResponseMessage {
    private final byte value;

    public GetBytePropertyResponseMessage(Object id, byte value) {
        super(id);
        this.value = value;
    }
}
