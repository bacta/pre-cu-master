package com.ocdsoft.bacta.quasar.messages;

import co.paralleluniverse.actors.behaviors.ResponseMessage;
import lombok.Getter;

/**
 * Created by crush on 3/31/2017.
 */
@Getter
public final class SetPropertyResponseMessage extends ResponseMessage {
    private final boolean success;
    private final String message;


    public SetPropertyResponseMessage(Object id, boolean success) {
        super(id);
        this.success = success;
        this.message = null;
    }

    public SetPropertyResponseMessage(Object id, boolean success, String message) {
        super(id);
        this.success = success;
        this.message = message;
    }
}
