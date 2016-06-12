package com.ocdsoft.bacta.swg.server.game.data.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.ocdsoft.bacta.engine.io.NetworkObjectByteSerializer;
import com.ocdsoft.bacta.engine.object.NetworkObject;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.StaticObject;
import com.ocdsoft.bacta.swg.server.game.object.cell.CellObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.IntangibleObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.manufacture.ManufactureSchematicObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.mission.MissionObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.player.PlayerObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.schematic.DraftSchematicObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.building.BuildingObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.factory.FactoryObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.installation.InstallationObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.installation.harvester.HarvesterInstallationObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.installation.manufacture.ManufactureInstallationObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.quest.PlayerQuestObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.resource.ResourceContainerObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.ship.ShipObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.vehicle.VehicleObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.weapon.WeaponObject;
import com.ocdsoft.bacta.swg.server.game.object.template.server.*;
import com.ocdsoft.bacta.swg.server.game.object.universe.UniverseObject;
import com.ocdsoft.bacta.swg.server.game.object.universe.group.GroupObject;
import com.ocdsoft.bacta.swg.server.game.object.universe.guild.GuildObject;
import com.ocdsoft.bacta.swg.server.game.object.universe.planet.PlanetObject;
import com.ocdsoft.bacta.swg.shared.collision.BarrierObject;
import com.ocdsoft.bacta.swg.shared.collision.DoorObject;
import com.ocdsoft.bacta.swg.shared.object.GameObject;
import com.ocdsoft.bacta.swg.shared.terrain.object.TerrainObject;
import de.javakaffee.kryoserializers.BitSetSerializer;
import gnu.trove.TIntCollection;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Created by kburkhardt on 7/25/14.
 */

public final class KryoSerializer implements NetworkObjectByteSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KryoSerializer.class);
    private final ThreadLocal<Kryo> kryos;
    private final GameObjectSerializer gameObjectSerializer;

    @Inject
    public KryoSerializer(final GameObjectSerializer gameObjectSerializer, final Injector injector) {

        this.gameObjectSerializer = gameObjectSerializer;

        kryos = new ThreadLocal<Kryo>() {
            protected Kryo initialValue() {
                Kryo kryo = new Kryo();

                //kryo.setRegistrationRequired(true);

                registerTypes(kryo, injector);

                return kryo;
            }
        };
        kryos.get();
    }

    private void registerTypes(final Kryo kryo, final Injector injector) {

        registerSwgObjects(kryo, injector);
        registerSwgTemplates(kryo, injector);

        kryo.register(BitSet.class, new BitSetSerializer());

        kryo.register(THashMap.class, new MapSerializer() {
            @Override
            protected Map create(Kryo kryo, Input input, Class<Map> type) {
                return new THashMap();
            }
        });

        kryo.register(TIntArrayList.class, new TIntCollectionSerializer() {
            @Override
            protected TIntCollection create(Kryo kryo, Input input, Class<TIntCollection> type) {
                return new TIntArrayList();
            }
        });

        kryo.register(ArrayList.class, new CollectionSerializer() {
            @Override
            protected Collection create(Kryo kryo, Input input, Class<Collection> type) {
                return new ArrayList();
            }
        });
    }

    private void registerSwgObjects(final Kryo kryo, final Injector injector) {

        final GameObjectReferenceSerializer gameObjectReferenceSerializer = injector.getInstance(GameObjectReferenceSerializer.class);
        kryo.register(GameObject.class, gameObjectReferenceSerializer);

        kryo.register(BarrierObject.class, gameObjectReferenceSerializer);
        kryo.register(BuildingObject.class, gameObjectReferenceSerializer);
        kryo.register(CellObject.class, gameObjectReferenceSerializer);
        kryo.register(CreatureObject.class, gameObjectReferenceSerializer);
        kryo.register(DoorObject.class, gameObjectReferenceSerializer);
        kryo.register(DraftSchematicObject.class, gameObjectReferenceSerializer);
        kryo.register(FactoryObject.class, gameObjectReferenceSerializer);
        kryo.register(GroupObject.class, gameObjectReferenceSerializer);
        kryo.register(GuildObject.class, gameObjectReferenceSerializer);
        kryo.register(HarvesterInstallationObject.class, gameObjectReferenceSerializer);
        kryo.register(InstallationObject.class, gameObjectReferenceSerializer);
        kryo.register(IntangibleObject.class, gameObjectReferenceSerializer);
        kryo.register(ManufactureInstallationObject.class, gameObjectReferenceSerializer);
        kryo.register(ManufactureSchematicObject.class, gameObjectReferenceSerializer);
        kryo.register(MissionObject.class, gameObjectReferenceSerializer);
        kryo.register(PlanetObject.class, gameObjectReferenceSerializer);
        kryo.register(PlayerObject.class, gameObjectReferenceSerializer);
        kryo.register(PlayerQuestObject.class, gameObjectReferenceSerializer);
        kryo.register(ResourceContainerObject.class, gameObjectReferenceSerializer);
        kryo.register(ServerObject.class, gameObjectReferenceSerializer);
        kryo.register(ShipObject.class, gameObjectReferenceSerializer);
        kryo.register(StaticObject.class, gameObjectReferenceSerializer);
        kryo.register(TangibleObject.class, gameObjectReferenceSerializer);
        kryo.register(TerrainObject.class, gameObjectReferenceSerializer);
        kryo.register(UniverseObject.class, gameObjectReferenceSerializer);
        kryo.register(VehicleObject.class, gameObjectReferenceSerializer);
        kryo.register(WeaponObject.class, gameObjectReferenceSerializer);

        final Reflections reflections = new Reflections();
        final Set<Class<? extends GameObject>> subTypes = reflections.getSubTypesOf(GameObject.class);
        subTypes.forEach(clazz -> {
            if( kryo.getRegistration(clazz) == null) {
                LOGGER.error("Object not registered {}", clazz.getSimpleName());
            }
        });
    }

    private void registerSwgTemplates(final Kryo kryo, final Injector injector) {

        ObjectTemplateSerializer objectTemplateSerializer = injector.getInstance(ObjectTemplateSerializer.class);
        kryo.register(ServerObjectTemplate.class, objectTemplateSerializer);

        kryo.register(ServerBattlefieldMarkerObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerBuildingObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerCellObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerCityObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerConstructionContractObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerCreatureObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerDraftSchematicObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerFactoryObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerGroupObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerGuildObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerHarvesterInstallationObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerInstallationObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerIntangibleObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerJediManagerObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerManufactureInstallationObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerManufactureSchematicObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerMissionBoardObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerMissionDataObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerMissionListEntryObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerMissionObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerPlanetObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerPlayerObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerPlayerQuestObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerResourceContainerObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerShipObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerStaticObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerTangibleObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerTokenObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerUniverseObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerVehicleObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerWeaponObjectTemplate.class, objectTemplateSerializer);
        kryo.register(ServerXpManagerObjectTemplate.class, objectTemplateSerializer);

        final Reflections reflections = new Reflections();
        final Set<Class<? extends ServerObjectTemplate>> templateSubTypes = reflections.getSubTypesOf(ServerObjectTemplate.class);
        templateSubTypes.forEach(clazz -> {
            if( kryo.getRegistration(clazz) == null) {
                LOGGER.error("Template class not registered {}", clazz.getSimpleName());
            }
        });
    }

    @Override
    public <T extends NetworkObject> byte[] serialize(T object) {
        try {
            Kryo kryo = kryos.get();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            UnsafeOutput output = new UnsafeOutput(stream);
            kryo.writeClass(output, object.getClass());
            kryo.writeObject(output, object, gameObjectSerializer);
            output.flush();
            output.close();
            return stream.toByteArray();
        } catch (Exception exception) {
            LOGGER.error("Error with class " + object.getClass().getName());
            throw new RuntimeException(exception);
        }
    }

    @Override
        public <T extends NetworkObject> T deserialize(byte[] bytes) {
        try {
            Kryo kryo = kryos.get();
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            UnsafeInput input = new UnsafeInput(stream);
            Registration registration = kryo.readClass(input);
            T result = (T) kryo.readObject(input, registration.getType(), gameObjectSerializer);
            input.close();
            return result;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
