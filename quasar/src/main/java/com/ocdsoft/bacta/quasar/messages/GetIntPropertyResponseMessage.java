package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.behaviors.ResponseMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class GetIntPropertyResponseMessage extends ResponseMessage {
    private final int value;

    public GetIntPropertyResponseMessage(Object id, int value) {
        super(id);
        this.value = value;
    }
}
