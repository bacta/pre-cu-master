package com.ocdsoft.bacta.swg.server.game.object.tangible.factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.swg.shared.database.NetworkIdGenerator;
import com.ocdsoft.bacta.engine.service.objectfactory.NetworkObjectFactory;
import com.ocdsoft.bacta.swg.server.game.object.GameObjectConstructorMap;
import com.ocdsoft.bacta.swg.server.game.object.template.server.ServerObjectTemplate;
import com.ocdsoft.bacta.swg.shared.container.SlotIdManager;
import com.ocdsoft.bacta.swg.shared.object.GameObject;
import com.ocdsoft.bacta.swg.shared.template.ObjectTemplateList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by kburkhardt on 2/24/14.
 */
@Singleton
public class GuiceNetworkObjectFactory implements NetworkObjectFactory<GameObject, ServerObjectTemplate> {

    private final ObjectTemplateList objectTemplateList;
    private final SlotIdManager slotIdManager;
    private final NetworkIdGenerator idGenerator;
    private final GameObjectConstructorMap constructorMap;


    @Inject
    public GuiceNetworkObjectFactory(final ObjectTemplateList objectTemplateList,
                                     final SlotIdManager slotIdManager,
                                     final NetworkIdGenerator idGenerator,
                                     final GameObjectConstructorMap constructorMap) {

        this.objectTemplateList = objectTemplateList;
        this.slotIdManager = slotIdManager;
        this.idGenerator = idGenerator;
        this.constructorMap = constructorMap;
    }

    @Override
    public <T extends GameObject> T createNetworkObject(Class<T> clazz, ServerObjectTemplate template) {

        Constructor<T> constructor = constructorMap.get(clazz);

        T newObject = null;
        try {

            newObject = constructor.newInstance(objectTemplateList, slotIdManager, template);
            newObject.setNetworkId(idGenerator.next());

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return newObject;
    }
}
