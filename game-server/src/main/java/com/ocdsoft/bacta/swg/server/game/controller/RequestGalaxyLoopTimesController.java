package com.ocdsoft.bacta.swg.server.game.controller;

import com.ocdsoft.bacta.soe.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.controller.MessageHandled;
import com.ocdsoft.bacta.swg.server.game.message.client.GalaxyLoopTimesResponse;
import com.ocdsoft.bacta.swg.server.game.message.client.RequestGalaxyLoopTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageHandled(handles = RequestGalaxyLoopTimes.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class RequestGalaxyLoopTimesController implements GameNetworkMessageController<RequestGalaxyLoopTimes> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGalaxyLoopTimesController.class);

    @Override
    public void handleIncoming(SoeUdpConnection connection, RequestGalaxyLoopTimes message) {
        // TODO: Frame millis, if possible
        connection.sendMessage(new GalaxyLoopTimesResponse(0, 0));
    }
}

