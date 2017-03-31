package com.ocdsoft.bacta.swg.server.game.message;

import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.soe.dispatch.MessageId;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 6/5/2016.
 */
@Getter
@Priority(0x05)
@MessageId(0x7ca18726) //"ObjectMenuSelectMessage::MESSAGE_TYPE"
@AllArgsConstructor
public final class ObjectMenuSelectMessage extends GameNetworkMessage {
    private final long playerId;
    private final short selectedItemId;

    public ObjectMenuSelectMessage(final ByteBuffer buffer) {
        this.playerId = buffer.getLong();
        this.selectedItemId = buffer.getShort();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, playerId);
        BufferUtil.put(buffer, selectedItemId);
    }
}