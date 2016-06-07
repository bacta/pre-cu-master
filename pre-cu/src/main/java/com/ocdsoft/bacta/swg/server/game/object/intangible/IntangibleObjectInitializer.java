package com.ocdsoft.bacta.swg.server.game.object.intangible;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.swg.server.game.object.ObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.object.ServerObjectInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by crush on 6/7/2016.
 */
@Singleton
public final class IntangibleObjectInitializer implements ObjectInitializer<IntangibleObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntangibleObjectInitializer.class);

    private final ServerObjectInitializer serverObjectInitializer;

    @Inject
    public IntangibleObjectInitializer(final ServerObjectInitializer serverObjectInitializer) {
        this.serverObjectInitializer = serverObjectInitializer;
    }

    @Override
    public void initializeFirstTimeObject(final IntangibleObject serverObject) {
        serverObjectInitializer.initializeFirstTimeObject(serverObject);

        LOGGER.warn("Not implemented");
    }

    @Override
    public void loadedFromDatabase(final IntangibleObject serverObject) {
        LOGGER.warn("Not implemented");
    }
}
