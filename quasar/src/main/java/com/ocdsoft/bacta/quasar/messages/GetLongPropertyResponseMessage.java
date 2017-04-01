package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.behaviors.ResponseMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class GetLongPropertyResponseMessage extends ResponseMessage {
    private final long value;

    public GetLongPropertyResponseMessage(Object id, long value) {
        super(id);
        this.value = value;
    }
}
