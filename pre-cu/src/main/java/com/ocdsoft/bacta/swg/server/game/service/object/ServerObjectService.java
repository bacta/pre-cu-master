package com.ocdsoft.bacta.swg.server.game.service.object;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.swg.shared.database.GameDatabaseConnector;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.service.object.ObjectService;
import com.ocdsoft.bacta.engine.service.objectfactory.NetworkObjectFactory;
import com.ocdsoft.bacta.swg.archive.OnDirtyCallbackBase;
import com.ocdsoft.bacta.swg.server.game.object.ObjectInitializer;
import com.ocdsoft.bacta.swg.server.game.object.ObjectInitializerProvider;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.template.server.ServerObjectTemplate;
import com.ocdsoft.bacta.swg.server.game.service.container.ContainerTransferService;
import com.ocdsoft.bacta.swg.server.game.service.data.ObjectTemplateService;
import com.ocdsoft.bacta.swg.shared.container.ContainerResult;
import com.ocdsoft.bacta.swg.shared.container.SlotIdManager;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kyle on 3/24/14.
 */
@Singleton
public final class ServerObjectService implements ObjectService<ServerObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerObjectService.class);

    private final TLongObjectMap<ServerObject> internalMap = new TLongObjectHashMap<>();

    private final Set<ServerObject> dirtyList = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final NetworkObjectFactory networkObjectFactory;
    private final ObjectInitializerProvider objectInitializerProvider;
    private final DeltaNetworkDispatcher deltaDispatcher;
    private final GameDatabaseConnector databaseConnector;
    private final ObjectTemplateService objectTemplateService;
    private final ContainerTransferService containerTransferService;
    private final SlotIdManager slotIdManager;
    private final int deltaUpdateInterval;

    @Inject
    public ServerObjectService(final BactaConfiguration configuration,
                               final ObjectInitializerProvider objectInitializerProvider,
                               final NetworkObjectFactory networkObjectFactory,
                               final GameDatabaseConnector databaseConnector,
                               final ObjectTemplateService objectTemplateService,
                               final SlotIdManager slotIdManager,
                               final ContainerTransferService containerTransferService) {

        this.objectInitializerProvider = objectInitializerProvider;
        this.networkObjectFactory = networkObjectFactory;
        this.deltaUpdateInterval = configuration.getIntWithDefault("Bacta/GameServer", "DeltaUpdateInterval", 50);
        this.databaseConnector = databaseConnector;
        this.objectTemplateService = objectTemplateService;
        this.slotIdManager = slotIdManager;
        this.deltaDispatcher = new DeltaNetworkDispatcher();
        this.containerTransferService = containerTransferService;

        new Thread(deltaDispatcher).start();
    }

    @Override
    public <T extends ServerObject> T createObject(final String templatePath) {
        return createObject(templatePath, null);
    }

    @Override
    public <T extends ServerObject> T createObject(final String templatePath, final ServerObject parent) {
        final T object = internalCreateObject(templatePath);

        if (object != null && parent != null) {
            final ContainerResult containerResult = new ContainerResult();
            if (!containerTransferService.transferItemToGeneralContainer(parent, object, null, containerResult))
                LOGGER.error("Unable to transfer object {} to parent object {} during creation. Error: {}",
                        object.getNetworkId(), parent.getNetworkId(), containerResult.getError());
        }

        return object;
    }

    public <T extends ServerObject> T createObjectInSlot(final String templatePath, final ServerObject parent, final int slotId) {
        final T object = internalCreateObject(templatePath);

        if (object != null && parent != null) {
            final ContainerResult containerResult = new ContainerResult();
            if (!containerTransferService.transferItemToSlottedContainerSlotId(parent, object, null, slotId, containerResult)) {
                LOGGER.error("Unable to transfer object {} to parent object {} in slot {} during creation. Error: {}",
                        object.getNetworkId(), parent.getNetworkId(), slotId, containerResult.getError());
            }
        }

        return object;
    }

    private <T extends ServerObject> T internalCreateObject(final String templatePath) {
        try {
            final ServerObjectTemplate serverObjectTemplate = objectTemplateService.getObjectTemplate(templatePath);
            final Class<T> objectClass = objectTemplateService.getClassForTemplate(serverObjectTemplate);
            final ObjectInitializer<T> objectInitializer = objectInitializerProvider.get(objectClass);

            //Create the object
            final T newObject = (T) networkObjectFactory.createNetworkObject(objectClass, serverObjectTemplate);
            databaseConnector.persist(newObject);

            //Initialize the object
            if (objectInitializer != null) {
                objectInitializer.initializeFirstTimeObject(newObject);
            } else {
                LOGGER.warn("No initializer is bound for class {}.", objectClass.getName());
            }

            newObject.setOnDirtyCallback(new ServerObjectServiceOnDirtyCallback(newObject));

            internalMap.put(newObject.getNetworkId(), newObject);

            return newObject;
        } catch (final Exception ex) {
            LOGGER.error("Exception creating object {}. Message: {}", templatePath, ex.getMessage());
            return null;
        }
    }

    @Override
    public <T extends ServerObject> T get(long key) {
        T object = (T) internalMap.get(key);

        if(object == null) {
           object = databaseConnector.get(key);
            if(object != null) {
                internalMap.put(key, object);
            }
        }

        return object;
    }

    @Override
    public <T extends ServerObject> T get(ServerObject requester, long key) {
        //TODO: Reimplement permissions.
        return get(key);
    }

    @Override
    public <T extends ServerObject> void updateObject(T object) {
        databaseConnector.persist(object);
    }


    // Executor?
    private class DeltaNetworkDispatcher implements Runnable {

        protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

        @Override
        public void run() {

            long nextIteration = 0;

            while(true) {
                try {
                    long currentTime = System.currentTimeMillis();

                    if (nextIteration > currentTime) {
                        Thread.sleep(nextIteration - currentTime);
                    }

                    for (ServerObject object : dirtyList) {
                        if (object.isInitialized())
                            object.sendDeltas();

                        object.clearDeltas();
                    }

                    dirtyList.clear();

                    nextIteration = currentTime + deltaUpdateInterval;

                } catch(Exception e) {
                    logger.error("UNKNOWN", e);
                }
            }
        }
    }

    private final class ServerObjectServiceOnDirtyCallback implements OnDirtyCallbackBase {
        private final ServerObject serverObject;

        public ServerObjectServiceOnDirtyCallback(final ServerObject serverObject) {
            this.serverObject = serverObject;
        }

        @Override
        public void onDirty() {
            dirtyList.add(serverObject);
        }

    }
}
