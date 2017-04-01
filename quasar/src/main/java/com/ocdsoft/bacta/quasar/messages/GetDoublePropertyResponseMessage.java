package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.behaviors.ResponseMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class GetDoublePropertyResponseMessage extends ResponseMessage {
    private final double value;

    public GetDoublePropertyResponseMessage(Object id, double value) {
        super(id);
        this.value = value;
    }
}
