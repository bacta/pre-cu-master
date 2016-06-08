package com.ocdsoft.bacta.swg.server.game.object;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.service.object.ObjectService;
import com.ocdsoft.bacta.swg.server.game.object.template.server.ServerObjectTemplate;
import com.ocdsoft.bacta.swg.server.game.script.ScriptService;
import com.ocdsoft.bacta.swg.server.game.service.object.ServerObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 6/6/2016.
 */
@Singleton
public final class ServerObjectInitializer implements ObjectInitializer<ServerObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerObjectInitializer.class);

    private final ObjectService<ServerObject> objectService;
    private final ScriptService scriptService;

    @Inject
    public ServerObjectInitializer(final ObjectService<ServerObject> objectService,
                                   final ScriptService scriptService) {
        this.objectService = objectService;
        this.scriptService = scriptService;
    }

    @Override
    public void initializeFirstTimeObject(final ServerObject serverObject) {
        LOGGER.debug("Initializing object [{}] for first time.", serverObject.getNetworkId());

        serverObject.setInitialized(true);
        serverObject.setPlacing(true);

        final ServerObjectTemplate serverObjectTemplate = (ServerObjectTemplate) serverObject.getObjectTemplate();
        assert serverObjectTemplate != null : "Tried to initialize object that had no ServerObjectTemplate.";


        //Do all the initializeFirstTimeObject logic here. Then we do the serverObjectInitializeFirstTimeObject logic.


        //Start serverObjectInitializeFirstTimeObject
        //Lets do the scripts.

        final int scriptsCount = serverObjectTemplate.getScriptsCount();

        //TEMPORARY HACK. Converting script names.
        for (int i = 0; i < scriptsCount; ++i) {
            final String scriptName = String.format("bacta/%s.clj", serverObjectTemplate.getScripts(i).replace('.', '/'));
            scriptService.attachScript(scriptName, serverObject);
        }
    }

    @Override
    public void loadedFromDatabase(final ServerObject serverObject) {
        //Nothing to do for server object.
        LOGGER.warn("Not implemented");
    }
}
