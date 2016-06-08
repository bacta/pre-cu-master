package com.ocdsoft.bacta.swg.server.chat;

import com.ocdsoft.bacta.soe.ServerState;
import com.ocdsoft.bacta.soe.service.PublisherService;
import com.ocdsoft.bacta.soe.io.udp.NetworkConfiguration;
import com.ocdsoft.bacta.soe.service.OutgoingConnectionService;

public class BaseChatModule extends ChatModule {

    @Override
    protected void configure() {
        bind(ServerState.class).to(ChatServerState.class);
        bind(NetworkConfiguration.class).to(ChatNetworkConfiguration.class);
        bind(OutgoingConnectionService.class).to(ChatServer.ChatOutgoingConnectionService.class);

        bind(SwgChatServer.class).asEagerSingleton();
    }

}
