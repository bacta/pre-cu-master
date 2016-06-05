package com.ocdsoft.bacta.swg.server.game.controller.client;

import com.google.inject.Inject;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.service.AccountService;
import com.ocdsoft.bacta.soe.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.controller.MessageHandled;
import com.ocdsoft.bacta.swg.server.game.message.ErrorMessage;
import com.ocdsoft.bacta.swg.server.game.message.creation.ClientCreateCharacter;
import com.ocdsoft.bacta.swg.server.game.message.creation.ClientCreateCharacterFailed;
import com.ocdsoft.bacta.swg.server.game.name.NameService;
import com.ocdsoft.bacta.swg.server.game.player.creation.CharacterCreationService;
import com.ocdsoft.bacta.swg.server.login.object.CharacterInfo;
import com.ocdsoft.bacta.swg.server.login.object.SoeAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageHandled(handles = ClientCreateCharacter.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public final class ClientCreateCharacterController implements GameNetworkMessageController<ClientCreateCharacter> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCreateCharacterController.class);

    private final AccountService<SoeAccount> accountService;
    private final CharacterCreationService characterCreationService;

    private final int secondsBetweenCreation;

    @Inject
    public ClientCreateCharacterController(final AccountService<SoeAccount> accountService,
                                           final CharacterCreationService characterCreationService,
                                           final BactaConfiguration bactaConfiguration) {

        this.accountService = accountService;
        this.characterCreationService = characterCreationService;

        //TODO: Move this off of here...
        this.secondsBetweenCreation = bactaConfiguration.getIntWithDefault(
                CharacterCreationService.CONFIG_SECTION, "minutesBetweenCharCreate", 15) * 60;
    }

    @Override
    public void handleIncoming(final SoeUdpConnection connection, final ClientCreateCharacter createMessage) {
        final SoeAccount account = accountService.getAccount(connection.getAccountUsername());

        if (account == null) {
            final ErrorMessage error = new ErrorMessage("Error", "Account not found.", false);
            connection.sendMessage(error);

            LOGGER.info("Account was not found.");
            return;
        }

        // check duration
        int secondsSinceLastCreation = (int) ((System.currentTimeMillis() - account.getLastCharacterCreationTime()) / 1000);
        account.setLastCharacterCreationTime(System.currentTimeMillis());
        accountService.updateAccount(account);

        if ((secondsSinceLastCreation < secondsBetweenCreation)) {
            ClientCreateCharacterFailed failed = new ClientCreateCharacterFailed(createMessage.getCharacterName(), NameService.NAME_DECLINED_TOO_FAST);
            connection.sendMessage(failed);
            return;
        }

        final CharacterInfo characterInfo = this.characterCreationService.createCharacter(connection, createMessage);


        if (characterInfo != null) {
            account.addCharacter(characterInfo);
            account.setLastCharacterCreationTime(System.currentTimeMillis());
            accountService.updateAccount(account);
        } else {
            //If characterInfo is null, then a character creation failed message was already sent.
            //TODO: Refactor all this when account service is removed and async calls are being made.
        }
    }
}

