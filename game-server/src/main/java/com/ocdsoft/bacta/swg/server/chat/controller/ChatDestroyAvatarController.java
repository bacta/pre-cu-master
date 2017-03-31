package com.ocdsoft.bacta.swg.server.chat.controller;

import com.google.inject.Inject;
import com.ocdsoft.bacta.soe.ServerType;
import com.ocdsoft.bacta.soe.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.controller.MessageHandled;
import com.ocdsoft.bacta.swg.server.chat.SwgChatServer;
import com.ocdsoft.bacta.swg.server.game.message.chat.ChatDestroyAvatar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 6/4/2016.
 */
@MessageHandled(handles = ChatDestroyAvatar.class, type = ServerType.CHAT)
@ConnectionRolesAllowed(value = ConnectionRole.WHITELISTED)
public class ChatDestroyAvatarController implements GameNetworkMessageController<ChatDestroyAvatar> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatDestroyAvatarController.class);

    private final SwgChatServer chatServer;

    @Inject
    public ChatDestroyAvatarController(final SwgChatServer chatServer) {
        this.chatServer = chatServer;
    }

    @Override
    public void handleIncoming(final SoeUdpConnection connection, final ChatDestroyAvatar message) throws Exception {
        this.chatServer.destroyAvatar(message.getFirstName());
    }
}