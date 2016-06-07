package com.ocdsoft.bacta.swg.server.game.data.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.ocdsoft.bacta.swg.server.game.data.serialize.GameObjectByteSerializer;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.player.PlayerObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObject;
import com.ocdsoft.bacta.swg.server.game.object.template.server.ServerObjectTemplate;
import com.ocdsoft.bacta.swg.shared.object.GameObject;
import de.javakaffee.kryoserializers.BitSetSerializer;
import gnu.trove.TIntCollection;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Created by kburkhardt on 7/25/14.
 */

public final class KryoSerializer implements GameObjectByteSerializer {

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

        kryo.register(BitSet.class, new BitSetSerializer());

        kryo.register(GameObject.class, injector.getInstance(GameObjectReferenceSerializer.class));
        kryo.register(ServerObjectTemplate.class, injector.getInstance(ObjectTemplateSerializer.class));
//        kryo.register(ServerObject.class, serializerFactory.create(ServerObject.class));
//        kryo.register(TangibleObject.class, serializerFactory.create(TangibleObject.class));
//        kryo.register(PlayerObject.class, serializerFactory.create(PlayerObject.class));

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
    public <T extends GameObject> byte[] serialize(T object) {
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
        public <T extends GameObject> T deserialize(byte[] bytes) {
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
