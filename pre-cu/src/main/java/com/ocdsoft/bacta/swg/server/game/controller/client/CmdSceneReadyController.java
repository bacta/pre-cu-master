package com.ocdsoft.bacta.swg.server.game.controller.client;

import com.google.inject.Inject;
import com.ocdsoft.bacta.engine.service.object.ObjectService;
import com.ocdsoft.bacta.soe.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.controller.MessageHandled;
import com.ocdsoft.bacta.soe.event.ConnectEvent;
import com.ocdsoft.bacta.swg.server.game.GameServerState;
import com.ocdsoft.bacta.swg.server.game.chat.GameChatService;
import com.ocdsoft.bacta.swg.server.game.message.client.CmdSceneReady;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.script.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageHandled(handles = CmdSceneReady.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class CmdSceneReadyController implements GameNetworkMessageController<CmdSceneReady> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdSceneReadyController.class);

    private final GameChatService chatService;
    private final GameServerState serverState;
    private final ObjectService<ServerObject> objectService;
    private final ScriptService scriptService;

    @Inject
    public CmdSceneReadyController(final GameChatService chatService,
                                   final GameServerState serverState,
                                   final ObjectService<ServerObject> objectService,
                                   final ScriptService scriptService) {
        this.chatService = chatService;
        this.serverState = serverState;
        this.objectService = objectService;
        this.scriptService = scriptService;
    }

    @Override
    public void handleIncoming(final SoeUdpConnection connection, final CmdSceneReady message) {
        final CmdSceneReady startScene = new CmdSceneReady();
        connection.sendMessage(startScene);

        //TODO: Putting the chat room enters here for now. Temporary until we get scripting in.
        final String galaxyName = serverState.getClusterServer().getName();
        final String planetName = "tatooine";

        LOGGER.warn("Hardcoded tatooine as planet name in CmdSceneReady. Readdress when planets are fleshed out.");

        chatService.enterRoom(connection.getCurrentCharName(), "swg.system", true, false);
        chatService.enterRoom(connection.getCurrentCharName(), String.format("swg.%s.system", galaxyName), true, false);
        chatService.enterRoom(connection.getCurrentCharName(), String.format("swg.%s.%s.system", galaxyName, planetName), true, false);



        //TEMPORARY HACK - Remove ASAP
        LOGGER.debug("Sending connected event to script service trigger.");
        final ServerObject serverObject = objectService.get(connection.getCurrentNetworkId());

        if (serverObject != null) {
            scriptService.triggerScript(serverObject, "bacta/player/base/base_player.clj", new ConnectEvent(connection));
        }
        else {
            LOGGER.error("Cant send event because server object is null.");
        }

    }
}

