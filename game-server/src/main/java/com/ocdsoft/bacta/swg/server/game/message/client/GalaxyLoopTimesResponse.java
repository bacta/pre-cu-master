package com.ocdsoft.bacta.swg.server.game.message.client;

import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/4/2016.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public class GalaxyLoopTimesResponse extends GameNetworkMessage {

    private final int currentFrameMilliseconds;
    private final int lastFrameMilliseconds;

    public GalaxyLoopTimesResponse(final ByteBuffer buffer) {
        currentFrameMilliseconds = buffer.getInt();
        lastFrameMilliseconds = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putInt(currentFrameMilliseconds);
        buffer.putInt(lastFrameMilliseconds);
    }
}