package com.ocdsoft.bacta.swg.server.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.soe.io.udp.BaseNetworkConfiguration;
import com.ocdsoft.bacta.soe.io.udp.NetworkConfiguration;

import java.net.UnknownHostException;

/**
 * Created by kyle on 4/12/2016.
 */

@Singleton
public final class ChatNetworkConfiguration extends BaseNetworkConfiguration implements NetworkConfiguration {

    @Inject
    public ChatNetworkConfiguration(final BactaConfiguration configuration) throws UnknownHostException {
        super(configuration, "Bacta/ChatServer");
    }
}
