package com.ocdsoft.bacta.swg.shared.chat.messages;

import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/20/2016.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public final class ChatUnbanAvatarFromRoom extends GameNetworkMessage {
    private final int sequence;
    private final ChatAvatarId avatarId;
    private final String roomName;

    public ChatUnbanAvatarFromRoom(final ByteBuffer buffer) {
        avatarId = new ChatAvatarId(buffer);
        roomName = BufferUtil.getAscii(buffer);
        sequence = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, avatarId);
        BufferUtil.putAscii(buffer, roomName);
        BufferUtil.put(buffer, sequence);
    }
}