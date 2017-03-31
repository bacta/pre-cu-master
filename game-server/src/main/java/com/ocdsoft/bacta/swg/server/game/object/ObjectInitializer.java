package com.ocdsoft.bacta.swg.server.game.object;

/**
 * Created by crush on 6/6/2016.
 */
public interface ObjectInitializer<T extends ServerObject> {
    void initializeFirstTimeObject(T serverObject);
    void loadedFromDatabase(T serverObject);
}
