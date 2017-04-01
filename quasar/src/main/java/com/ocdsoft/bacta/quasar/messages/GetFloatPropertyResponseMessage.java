package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.behaviors.ResponseMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class GetFloatPropertyResponseMessage extends ResponseMessage {
    private final float value;

    public GetFloatPropertyResponseMessage(Object id, float value) {
        super(id);
        this.value = value;
    }
}
