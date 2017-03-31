package com.ocdsoft.bacta.swg.server.game.message.chat;

import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 6/4/2016.
 *
 * Requests that the avatar with the first name be destroyed.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public final class ChatDestroyAvatar extends GameNetworkMessage {
    private final String firstName;

    public ChatDestroyAvatar(final ByteBuffer buffer) {
        this.firstName = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, firstName);
    }
}