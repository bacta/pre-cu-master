package com.ocdsoft.bacta.swg.server.game.object.tangible;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.service.object.ObjectService;
import com.ocdsoft.bacta.swg.server.game.object.ObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.ServerObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.service.object.ServerObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 6/7/2016.
 */
@Singleton
public final class TangibleObjectInitializer implements ObjectInitializer<TangibleObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TangibleObjectInitializer.class);

    private final ServerObjectInitializer serverObjectInitializer;
    private final ObjectService<ServerObject> serverObjectService;

    @Inject
    public TangibleObjectInitializer(final ObjectService<ServerObject> serverObjectService,
                                     final ServerObjectInitializer serverObjectInitializer) {
        this.serverObjectInitializer = serverObjectInitializer;
        this.serverObjectService = serverObjectService;
    }

    @Override
    public void initializeFirstTimeObject(final TangibleObject serverObject) {
        LOGGER.warn("Not implemented.");
        //Setup armor from template.
        //Attach the combat skeleton script to this object
        //Handle container setup.
        serverObjectInitializer.initializeFirstTimeObject(serverObject);
        //Calculate Pvpable state.
    }

    @Override
    public void loadedFromDatabase(final TangibleObject serverObject) {
        serverObjectInitializer.loadedFromDatabase(serverObject);

        //If we are a crafting tool that is counting down to a prototype, reactive the counter.
        //Calc pvpable state
        //if is locked, read user access list and guild access list
        //Check for invalid sockets
        LOGGER.warn("Not implemented.");
    }
}