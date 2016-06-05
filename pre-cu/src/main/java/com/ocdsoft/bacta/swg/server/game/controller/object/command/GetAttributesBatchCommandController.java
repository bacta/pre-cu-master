package com.ocdsoft.bacta.swg.server.game.controller.object.command;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.server.game.controller.object.CommandQueueController;
import com.ocdsoft.bacta.swg.server.game.controller.object.QueuesCommand;
import com.ocdsoft.bacta.swg.server.game.message.AttributeListMessage;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.service.object.ServerObjectService;
import com.ocdsoft.bacta.swg.shared.util.CommandParamsIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by crush on 6/5/2016.
 */
@QueuesCommand("getAttributesBatch")
public final class GetAttributesBatchCommandController implements CommandQueueController {
    private static Logger LOGGER = LoggerFactory.getLogger(GetAttributesBatchCommandController.class);

    private final ServerObjectService serverObjectService;

    @Inject
    public GetAttributesBatchCommandController(final ServerObjectService serverObjectService) {
        this.serverObjectService = serverObjectService;
    }

    @Override
    public void handleCommand(final SoeUdpConnection connection, final ServerObject actor, final ServerObject target, final String params) {
        final CommandParamsIterator iterator = new CommandParamsIterator(params);

        final long objectNetworkId = iterator.getLong();
        final int clientRevision = iterator.getInteger();

        LOGGER.warn("Not implemented. Received request to get attributes in a batch for object {} and client revision {}.",
                objectNetworkId,
                clientRevision);

        // an arbitrary value that is not the same as the initial client revision on the game client (ObjectAttributesManager.cpp).
        int serverRevision = -127;
        boolean forceAttributeUpdate = false;

        final ServerObject obj = serverObjectService.get(objectNetworkId);

        if (obj != null) {
            //serverRevision = obj.getAttributeRevision();
            //if (obj.hasAttributeCaching() && !player.isCrafting()) {
            //  if (serverRevision != clientRevision)
            //      data = obj.getAttributes(playerId);
            //} else {
            //  data = obj.getAttributes(player);
            //  forceAttributeUpdate = true;
            //}

            //Just sending an empty list for now until we implement item attributes.

            if (serverRevision != clientRevision || forceAttributeUpdate) {
                final List<AttributeListMessage.AttributeKeyValuePair> data = ImmutableList.of();
                final AttributeListMessage alm = new AttributeListMessage(objectNetworkId, "", data, serverRevision);
                connection.sendMessage(alm);
            }
        }
    }
}