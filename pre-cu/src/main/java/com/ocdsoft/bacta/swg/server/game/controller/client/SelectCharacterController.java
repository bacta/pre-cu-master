package com.ocdsoft.bacta.swg.server.game.controller.client;

import com.google.inject.Inject;
import com.ocdsoft.bacta.engine.service.object.ObjectService;
import com.ocdsoft.bacta.soe.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.controller.MessageHandled;
import com.ocdsoft.bacta.soe.io.udp.PublisherService;
import com.ocdsoft.bacta.swg.server.game.GameServerState;
import com.ocdsoft.bacta.swg.server.game.chat.GameChatService;
import com.ocdsoft.bacta.swg.server.game.event.PlayerOnlineEvent;
import com.ocdsoft.bacta.swg.server.game.guild.GuildService;
import com.ocdsoft.bacta.swg.server.game.message.client.ParametersMessage;
import com.ocdsoft.bacta.swg.server.game.message.client.SelectCharacter;
import com.ocdsoft.bacta.swg.server.game.message.client.ServerTimeMessage;
import com.ocdsoft.bacta.swg.server.game.message.scene.CmdStartScene;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObject;
import com.ocdsoft.bacta.swg.server.game.scene.Scene;
import com.ocdsoft.bacta.swg.server.game.scene.UniverseSceneService;
import com.ocdsoft.bacta.swg.server.game.service.AccountSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@MessageHandled(handles = SelectCharacter.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public final class SelectCharacterController implements GameNetworkMessageController<SelectCharacter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectCharacterController.class);

    private final AccountSecurityService accountSecurityService;
    private final ObjectService<ServerObject> objectService;
    private final GuildService guildService;
    private final GameServerState serverState;
    private final GameChatService chatService;
    private final PublisherService publisherService;
    private final UniverseSceneService universeSceneService;

    @Inject
    public SelectCharacterController(final AccountSecurityService accountSecurityService,
                                     final ObjectService<ServerObject> objectService,
                                     final GuildService guildService,
                                     final GameServerState serverState,
                                     final GameChatService chatService,
                                     final PublisherService publisherService,
                                     final UniverseSceneService universeSceneService) {

        this.accountSecurityService = accountSecurityService;
        this.objectService = objectService;
        this.guildService = guildService;
        this.chatService = chatService;
        this.serverState = serverState;
        this.universeSceneService = universeSceneService;
        this.publisherService = publisherService;
    }

    @Override
    public void handleIncoming(final SoeUdpConnection connection, final SelectCharacter message) {

        if (accountSecurityService.verifyCharacterOwnership(connection, message.getCharacterId())) {

            CreatureObject character = objectService.get(message.getCharacterId());
            if (character != null) {

                connection.setCurrentNetworkId(character.getNetworkId());
                connection.setCurrentCharName(character.getAssignedObjectName());

                character.setConnection(connection);

                //Tell the ChatService to start connecting this character.
                chatService.connectAvatar(character);

                //TODO: Load the actual scene they are on.
                final Scene scene = universeSceneService.getDefaultScene();

                final CmdStartScene start = new CmdStartScene(
                        false,
                        character.getNetworkId(),
                        scene.getTerrainFileName(),
                        character.getTransformObjectToWorld().getPositionInParent(),
                        character.getObjectFrameKInWorld().theta(),
                        character.getSharedTemplate().getResourceName(),
                        0,
                        0);

                connection.sendMessage(start);

                final ServerTimeMessage serverTimeMessage = new ServerTimeMessage(0);
                connection.sendMessage(serverTimeMessage);

                //TODO: Read the weather update interval from either the config, or a weather service directly.
                //This message just tells the client how often to check for new weather.
                final ParametersMessage parametersMessage = new ParametersMessage(900); //seconds
                connection.sendMessage(parametersMessage);

                //Send guild object to character.
                guildService.sendTo(character);

                final Set<SoeUdpConnection> user = new HashSet<>();
                user.add(connection);

                scene.add(character); //Let's add them to scene before the baselines are sent? Maybe this should trigger sending of baselines?

                character.sendCreateAndBaselinesTo(user);

                publisherService.onEvent(new PlayerOnlineEvent(character));

            } else {
                LOGGER.error("Unable to lookup character {} ", message.getCharacterId());
            }
        }
    }
}

