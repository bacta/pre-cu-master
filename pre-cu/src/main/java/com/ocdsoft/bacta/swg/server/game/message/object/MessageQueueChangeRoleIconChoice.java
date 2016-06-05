package com.ocdsoft.bacta.swg.server.game.message.object;

import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.swg.server.game.controller.object.GameControllerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 6/4/2016.
 */
@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.CHANGE_ROLE_ICON_CHOICE)
public final class MessageQueueChangeRoleIconChoice implements MessageQueueData {
    private final int roleIconChoice;
    private final byte sequenceId;

    public MessageQueueChangeRoleIconChoice(final ByteBuffer buffer) {
        roleIconChoice = buffer.getInt();
        sequenceId = buffer.get();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, roleIconChoice);
        BufferUtil.put(buffer, sequenceId);
    }
}
