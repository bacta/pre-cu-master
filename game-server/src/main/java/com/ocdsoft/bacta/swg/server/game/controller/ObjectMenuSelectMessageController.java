package com.ocdsoft.bacta.swg.server.game.controller;

import com.ocdsoft.bacta.soe.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.controller.MessageHandled;
import com.ocdsoft.bacta.swg.server.game.message.ObjectMenuSelectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 6/5/2016.
 */
@MessageHandled(handles = ObjectMenuSelectMessage.class)
@ConnectionRolesAllowed(value = ConnectionRole.AUTHENTICATED)
public class ObjectMenuSelectMessageController implements GameNetworkMessageController<ObjectMenuSelectMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMenuSelectMessageController.class);

    @Override
    public void handleIncoming(final SoeUdpConnection connection, final ObjectMenuSelectMessage message) throws Exception {
        LOGGER.warn("Not implemented");
    }
}