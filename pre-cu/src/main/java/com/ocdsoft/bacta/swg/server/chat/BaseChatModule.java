package com.ocdsoft.bacta.swg.server.chat;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.ocdsoft.bacta.soe.ServerState;
import com.ocdsoft.bacta.soe.io.udp.PublisherService;
import com.ocdsoft.bacta.soe.io.udp.NetworkConfiguration;
import com.ocdsoft.bacta.soe.service.OutgoingConnectionService;
import com.ocdsoft.bacta.swg.server.chat.ChatNetworkConfiguration;
import com.ocdsoft.bacta.swg.server.chat.ChatServer;
import com.ocdsoft.bacta.swg.server.chat.ChatServerState;
import com.ocdsoft.bacta.swg.server.chat.SwgChatServer;
import com.ocdsoft.bacta.swg.server.chat.service.ChatPublisherService;

public class BaseChatModule extends ChatModule {

    @Override
    protected void configure() {
        bind(ServerState.class).to(ChatServerState.class);
        bind(NetworkConfiguration.class).to(ChatNetworkConfiguration.class);
        bind(OutgoingConnectionService.class).to(ChatServer.ChatOutgoingConnectionService.class);
        bind(PublisherService.class).to(ChatPublisherService.class);

        bind(SwgChatServer.class).asEagerSingleton();
    }

}
