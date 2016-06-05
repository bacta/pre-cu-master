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
import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.swg.archive.delta.*;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.ServerObjectConstructorMap;
import com.ocdsoft.bacta.swg.server.game.object.intangible.player.PlayerObject;
import com.ocdsoft.bacta.swg.server.game.object.matchmaking.MatchMakingId;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObject;
import com.ocdsoft.bacta.swg.server.game.object.universe.group.GroupInviter;
import com.ocdsoft.bacta.swg.server.game.service.data.ObjectTemplateService;
import com.ocdsoft.bacta.swg.shared.container.SlotIdManager;
import com.ocdsoft.bacta.swg.shared.localization.StringId;
import com.ocdsoft.bacta.swg.shared.template.ObjectTemplateList;
import de.javakaffee.kryoserializers.BitSetSerializer;
import gnu.trove.TIntCollection;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Created by kburkhardt on 7/25/14.
 */

public final class KryoSerializer implements GameObjectSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KryoSerializer.class);
    private final ThreadLocal<Kryo> kryos;
    private final ObjectTemplateList objectTemplateList;
    private final SlotIdManager slotIdManager;
    private final ServerObjectConstructorMap constructorMap;
    private final ObjectTemplateService objectTemplateService;

    @Inject
    public KryoSerializer(final Injector injector,
                          final ObjectTemplateList objectTemplateList,
                          final SlotIdManager slotIdManager,
                          final ServerObjectConstructorMap constructorMap,
                          final ObjectTemplateService objectTemplateService) {

        this.objectTemplateList = objectTemplateList;
        this.slotIdManager = slotIdManager;
        this.constructorMap = constructorMap;
        this.objectTemplateService =objectTemplateService;

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

    private void registerTypes(Kryo kryo, Injector injector) {

        kryo.register(BitSet.class, new BitSetSerializer());

        kryo.register(CreatureObject.class, new NetworkObjectSerializer<CreatureObject>(
                CreatureObject.class, objectTemplateList, slotIdManager, constructorMap, objectTemplateService));
        kryo.register(ServerObject.class, new NetworkObjectSerializer<ServerObject>(
                ServerObject.class, objectTemplateList, slotIdManager, constructorMap, objectTemplateService));
        kryo.register(TangibleObject.class, new NetworkObjectSerializer<TangibleObject>(
                TangibleObject.class, objectTemplateList, slotIdManager, constructorMap, objectTemplateService));
        kryo.register(PlayerObject.class, new NetworkObjectSerializer<PlayerObject>(
                PlayerObject.class, objectTemplateList, slotIdManager, constructorMap, objectTemplateService));


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
    }

    @Override
    public final byte[] serialize(Object object) {
        try {
            Kryo kryo = kryos.get();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            UnsafeOutput output = new UnsafeOutput(stream);
            kryo.writeClass(output, object.getClass());
            kryo.writeObject(output, object);
            output.flush();
            LOGGER.trace(object.getClass().getName());
            LOGGER.trace(BufferUtil.bytesToHex(output.getBuffer(), ' '));
            output.close();
            return stream.toByteArray();
        } catch (Exception exception) {
            LOGGER.error("Error with class " + object.getClass().getName());
            throw new RuntimeException(exception);
        }
    }

    @Override
    public final Object deserialize(byte[] bytes) {
        try {
            Kryo kryo = kryos.get();
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            UnsafeInput input = new UnsafeInput(stream);
            Registration registration = kryo.readClass(input);
            LOGGER.trace(registration.getType().getName());
            LOGGER.trace(BufferUtil.bytesToHex(bytes, ' '));
            Object result = kryo.readObject(input, registration.getType());
            input.close();
            return result;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
