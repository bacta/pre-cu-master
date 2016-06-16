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

                // Require explicit registration
                kryo.setRegistrationRequired(true);
                registerTypes(kryo, injector);
                return kryo;
            }
        };
        kryos.get();
    }

    private void registerTypes(final Kryo kryo, final Injector injector) {

        // Kryo uses type 1-8 for java types

        // Reserves types 9-59
        registerSwgObjects(kryo, injector);

        // Reserves types 60-199
        registerSwgTemplates(kryo, injector);

        // Types start at 200
        kryo.register(BitSet.class, new BitSetSerializer(), 200);

        kryo.register(THashMap.class, new MapSerializer() {
            @Override
            protected Map create(Kryo kryo, Input input, Class<Map> type) {
                return new THashMap();
            }
        }, 201);

        kryo.register(TIntArrayList.class, new TIntCollectionSerializer() {
            @Override
            protected TIntCollection create(Kryo kryo, Input input, Class<TIntCollection> type) {
                return new TIntArrayList();
            }
        }, 202);

        kryo.register(ArrayList.class, new CollectionSerializer() {
            @Override
            protected Collection create(Kryo kryo, Input input, Class<Collection> type) {
                return new ArrayList();
            }
        }, 203);
    }

    private void registerSwgObjects(final Kryo kryo, final Injector injector) {

        final GameObjectReferenceSerializer gameObjectReferenceSerializer = injector.getInstance(GameObjectReferenceSerializer.class);
        kryo.register(GameObject.class, gameObjectReferenceSerializer, 9);

        kryo.register(BarrierObject.class, gameObjectReferenceSerializer, 10);
        kryo.register(BuildingObject.class, gameObjectReferenceSerializer, 11);
        kryo.register(CellObject.class, gameObjectReferenceSerializer, 12);
        kryo.register(CreatureObject.class, gameObjectReferenceSerializer, 13);
        kryo.register(DoorObject.class, gameObjectReferenceSerializer, 14);
        kryo.register(DraftSchematicObject.class, gameObjectReferenceSerializer, 15);
        kryo.register(FactoryObject.class, gameObjectReferenceSerializer, 16);
        kryo.register(GroupObject.class, gameObjectReferenceSerializer, 17);
        kryo.register(GuildObject.class, gameObjectReferenceSerializer, 18);
        kryo.register(HarvesterInstallationObject.class, gameObjectReferenceSerializer, 19);
        kryo.register(InstallationObject.class, gameObjectReferenceSerializer, 20);
        kryo.register(IntangibleObject.class, gameObjectReferenceSerializer, 21);
        kryo.register(ManufactureInstallationObject.class, gameObjectReferenceSerializer, 22);
        kryo.register(ManufactureSchematicObject.class, gameObjectReferenceSerializer, 23);
        kryo.register(MissionObject.class, gameObjectReferenceSerializer, 24);
        kryo.register(PlanetObject.class, gameObjectReferenceSerializer, 25);
        kryo.register(PlayerObject.class, gameObjectReferenceSerializer, 26);
        kryo.register(PlayerQuestObject.class, gameObjectReferenceSerializer, 27);
        kryo.register(ResourceContainerObject.class, gameObjectReferenceSerializer, 28);
        kryo.register(ServerObject.class, gameObjectReferenceSerializer, 29);
        kryo.register(ShipObject.class, gameObjectReferenceSerializer, 30);
        kryo.register(StaticObject.class, gameObjectReferenceSerializer, 31);
        kryo.register(TangibleObject.class, gameObjectReferenceSerializer, 32);
        kryo.register(TerrainObject.class, gameObjectReferenceSerializer, 33);
        kryo.register(UniverseObject.class, gameObjectReferenceSerializer, 34);
        kryo.register(VehicleObject.class, gameObjectReferenceSerializer, 35);
        kryo.register(WeaponObject.class, gameObjectReferenceSerializer, 36);

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
        kryo.register(ServerObjectTemplate.class, objectTemplateSerializer, 60);

        kryo.register(ServerBattlefieldMarkerObjectTemplate.class, objectTemplateSerializer, 61);
        kryo.register(ServerBuildingObjectTemplate.class, objectTemplateSerializer, 62);
        kryo.register(ServerCellObjectTemplate.class, objectTemplateSerializer, 63);
        kryo.register(ServerCityObjectTemplate.class, objectTemplateSerializer, 64);
        kryo.register(ServerConstructionContractObjectTemplate.class, objectTemplateSerializer, 65);
        kryo.register(ServerCreatureObjectTemplate.class, objectTemplateSerializer, 66);
        kryo.register(ServerDraftSchematicObjectTemplate.class, objectTemplateSerializer, 67);
        kryo.register(ServerFactoryObjectTemplate.class, objectTemplateSerializer, 68);
        kryo.register(ServerGroupObjectTemplate.class, objectTemplateSerializer, 69);
        kryo.register(ServerGuildObjectTemplate.class, objectTemplateSerializer, 70);
        kryo.register(ServerHarvesterInstallationObjectTemplate.class, objectTemplateSerializer, 71);
        kryo.register(ServerInstallationObjectTemplate.class, objectTemplateSerializer, 72);
        kryo.register(ServerIntangibleObjectTemplate.class, objectTemplateSerializer, 73);
        kryo.register(ServerJediManagerObjectTemplate.class, objectTemplateSerializer, 74);
        kryo.register(ServerManufactureInstallationObjectTemplate.class, objectTemplateSerializer, 75);
        kryo.register(ServerManufactureSchematicObjectTemplate.class, objectTemplateSerializer, 76);
        kryo.register(ServerMissionBoardObjectTemplate.class, objectTemplateSerializer, 77);
        kryo.register(ServerMissionDataObjectTemplate.class, objectTemplateSerializer, 78);
        kryo.register(ServerMissionListEntryObjectTemplate.class, objectTemplateSerializer, 79);
        kryo.register(ServerMissionObjectTemplate.class, objectTemplateSerializer, 80);
        kryo.register(ServerPlanetObjectTemplate.class, objectTemplateSerializer, 81);
        kryo.register(ServerPlayerObjectTemplate.class, objectTemplateSerializer, 82);
        kryo.register(ServerPlayerQuestObjectTemplate.class, objectTemplateSerializer, 83);
        kryo.register(ServerResourceContainerObjectTemplate.class, objectTemplateSerializer, 84);
        kryo.register(ServerShipObjectTemplate.class, objectTemplateSerializer, 85);
        kryo.register(ServerStaticObjectTemplate.class, objectTemplateSerializer, 86);
        kryo.register(ServerTangibleObjectTemplate.class, objectTemplateSerializer, 87);
        kryo.register(ServerTokenObjectTemplate.class, objectTemplateSerializer, 88);
        kryo.register(ServerUniverseObjectTemplate.class, objectTemplateSerializer, 89);
        kryo.register(ServerVehicleObjectTemplate.class, objectTemplateSerializer, 90);
        kryo.register(ServerWeaponObjectTemplate.class, objectTemplateSerializer, 91);
        kryo.register(ServerXpManagerObjectTemplate.class, objectTemplateSerializer, 92);

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
