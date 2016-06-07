package com.ocdsoft.bacta.swg.server.game.object;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.swg.server.game.object.intangible.IntangibleObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.IntangibleObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.object.intangible.player.PlayerObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.player.PlayerObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObjectInitializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by crush on 6/7/2016.
 *
 * Just a guice provider type class for creating an object initializer instance, or getting one that already exists.
 */
@Singleton
public final class GuiceObjectInitializerProvider implements ObjectInitializerProvider {
    private final Map<Class<? extends ServerObject>, ObjectInitializer<? extends ServerObject>> classToInitializersMap;

    @Inject
    public GuiceObjectInitializerProvider(final Injector injector) {
        classToInitializersMap = ImmutableMap.copyOf(createBindings(injector));
    }

    @SuppressWarnings("unchecked")
    public <T extends ServerObject> ObjectInitializer<T> get(final Class<T> classType) {
        return (ObjectInitializer<T>) classToInitializersMap.get(classType);
    }


    private Map<Class<? extends ServerObject>, ObjectInitializer<? extends ServerObject>> createBindings(final Injector injector) {
        final Map<Class<? extends ServerObject>, ObjectInitializer<? extends ServerObject>> map = new HashMap<>();

        map.put(ServerObject.class, injector.getInstance(ServerObjectInitializer.class));

        //Tangibles
        map.put(TangibleObject.class, injector.getInstance(TangibleObjectInitializer.class));
        map.put(CreatureObject.class, injector.getInstance(CreatureObjectInitializer.class));

        //Intangibles
        map.put(IntangibleObject.class, injector.getInstance(IntangibleObjectInitializer.class));
        map.put(PlayerObject.class, injector.getInstance(PlayerObjectInitializer.class));
        return map;
    }
}
