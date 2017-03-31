package com.ocdsoft.bacta.swg.server.game.object.intangible.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.swg.server.game.object.ObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.object.intangible.IntangibleObjectInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 6/7/2016.
 */
@Singleton
public final class PlayerObjectInitializer implements ObjectInitializer<PlayerObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerObjectInitializer.class);
    private final IntangibleObjectInitializer intangibleObjectInitializer;

    @Inject
    public PlayerObjectInitializer(final IntangibleObjectInitializer intangibleObjectInitializer) {
        this.intangibleObjectInitializer = intangibleObjectInitializer;
    }

    @Override
    public void initializeFirstTimeObject(final PlayerObject serverObject) {
        intangibleObjectInitializer.initializeFirstTimeObject(serverObject);

        LOGGER.warn("Not implemented");
    }

    @Override
    public void loadedFromDatabase(final PlayerObject serverObject) {
        LOGGER.warn("Not implemented");
    }
}