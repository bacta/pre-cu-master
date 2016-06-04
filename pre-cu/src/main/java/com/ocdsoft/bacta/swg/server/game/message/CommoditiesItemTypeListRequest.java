package com.ocdsoft.bacta.swg.server.game.message;

import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@Priority(0x2)
@AllArgsConstructor
public final class CommoditiesItemTypeListRequest extends GameNetworkMessage {
    private final String itemTypeMapVersionNumber;

    public CommoditiesItemTypeListRequest(final ByteBuffer buffer) {
        this.itemTypeMapVersionNumber = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, itemTypeMapVersionNumber);
    }
}
