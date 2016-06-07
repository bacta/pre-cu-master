package com.ocdsoft.bacta.swg.server.game.object;

/**
 * Created by crush on 6/7/2016.
 */
public interface ObjectInitializerProvider {
    <T extends ServerObject> ObjectInitializer<T> get(final Class<T> classType);
}
