package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.behaviors.ResponseMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class GetObjectPropertyResponseMessage<T> extends ResponseMessage {
    private final T value;

    public GetObjectPropertyResponseMessage(Object id, T value) {
        super(id);
        this.value = value;
    }
}
