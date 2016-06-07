package com.ocdsoft.bacta.swg.server.game.object.tangible.creature;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.service.object.ObjectService;
import com.ocdsoft.bacta.swg.server.game.object.ObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.service.object.ServerObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 6/7/2016.
 */
@Singleton
public final class CreatureObjectInitializer implements ObjectInitializer<CreatureObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreatureObjectInitializer.class);

    private final ObjectService<ServerObject> serverObjectService;
    private final TangibleObjectInitializer tangibleObjectInitializer;

    @Inject
    public CreatureObjectInitializer(final TangibleObjectInitializer tangibleObjectInitializer,
                                     final ObjectService<ServerObject> serverObjectService) {
        this.serverObjectService = serverObjectService;
        this.tangibleObjectInitializer = tangibleObjectInitializer;
    }

    @Override
    public void initializeFirstTimeObject(final CreatureObject creatureObject) {
        tangibleObjectInitializer.initializeFirstTimeObject(creatureObject);

        LOGGER.warn("Not implemented.");
        //updateMovementInfo
        //initializeDefaultWeapon
        //set current weapon
        //if still no weapon, error.
        //set object name to random name based on name generator type
        //set ownerId to self
        //recompute slope mod percent
        //setup skill data
        //pack wearables
        //set the bank container not to load contents
    }

    @Override
    public void loadedFromDatabase(final CreatureObject creatureObject) {
        LOGGER.warn("Not implemented.");
    }
}
