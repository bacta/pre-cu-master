package com.ocdsoft.bacta.swg.server.login;

import com.ocdsoft.bacta.soe.ServerState;
import com.ocdsoft.bacta.soe.service.PublisherService;
import com.ocdsoft.bacta.soe.io.udp.NetworkConfiguration;
import com.ocdsoft.bacta.soe.service.OutgoingConnectionService;

public class BaseLoginModule extends LoginModule {

	@Override
	protected void configure() {

		bind(ServerState.class).to(LoginServerState.class);
		bind(NetworkConfiguration.class).to(LoginNetworkConfiguration.class);
		bind(OutgoingConnectionService.class).to(LoginServer.LoginOutgoingConnectionService.class);

	}

}
