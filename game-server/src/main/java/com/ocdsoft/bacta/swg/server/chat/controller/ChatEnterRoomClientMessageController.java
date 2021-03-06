package com.ocdsoft.bacta.swg.server.chat.controller;

import com.google.inject.Inject;
import com.ocdsoft.bacta.soe.ServerType;
import com.ocdsoft.bacta.soe.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.controller.GameClientMessageController;
import com.ocdsoft.bacta.soe.controller.MessageHandled;
import com.ocdsoft.bacta.swg.server.chat.SwgChatServer;
import com.ocdsoft.bacta.swg.shared.chat.messages.ChatEnterRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 6/1/2016.
 */
@MessageHandled(handles = ChatEnterRoom.class, type = ServerType.CHAT)
@ConnectionRolesAllowed(ConnectionRole.WHITELISTED)
public final class ChatEnterRoomClientMessageController implements GameClientMessageController<ChatEnterRoom> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatEnterRoomClientMessageController.class);

    private final SwgChatServer chatServer;

    @Inject
    public ChatEnterRoomClientMessageController(final SwgChatServer chatServer) {
        this.chatServer = chatServer;
    }

    @Override
    public void handleIncoming(final long[] distributionList, final boolean reliable, final ChatEnterRoom message, final SoeUdpConnection connection) throws Exception {
        for (final long networkId : distributionList) {
            chatServer.enterRoom(networkId, message.getSequence(), message.getRoomName());
        }
    }
}
