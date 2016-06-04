package com.ocdsoft.bacta.swg.server.game.data.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.swg.archive.delta.*;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.player.PlayerObject;
import com.ocdsoft.bacta.swg.server.game.object.matchmaking.MatchMakingId;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObject;
import com.ocdsoft.bacta.swg.server.game.object.universe.group.GroupInviter;
import com.ocdsoft.bacta.swg.shared.localization.StringId;
import de.javakaffee.kryoserializers.BitSetSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by kburkhardt on 7/25/14.
 */

public final class KryoSerializer implements GameObjectSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KryoSerializer.class);
    private final ThreadLocal<Kryo> kryos;

    @Inject
    public KryoSerializer(final Injector injector) {

        kryos = new ThreadLocal<Kryo>() {
            protected Kryo initialValue() {
                Kryo kryo = new Kryo();

                kryo.setRegistrationRequired(true);

                registerTypes(kryo, injector);

                return kryo;
            }
        };
        kryos.get();
    }

    private void registerTypes(Kryo kryo, Injector injector) {
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(TreeSet.class);


        kryo.register(AutoDeltaFloat.class);
        kryo.register(AutoDeltaByteStream.class);
        kryo.register(AutoDeltaVariable.class);
        kryo.register(AutoDeltaInt.class);
        kryo.register(AutoDeltaString.class);
        kryo.register(AutoDeltaBoolean.class);
        kryo.register(AutoDeltaLong.class);
        kryo.register(AutoDeltaByte.class);
        kryo.register(AutoDeltaShort.class);

        kryo.register(GroupInviter.class);
        kryo.register(MatchMakingId.class);
        kryo.register(BitSet.class, new BitSetSerializer());
        kryo.register(Quat4f.class);
        kryo.register(Vector3f.class);
        kryo.register(StringId.class);

        kryo.register(CreatureObject.class, new NetworkObjectSerializer<CreatureObject>(CreatureObject.class));
        kryo.register(ServerObject.class, injector.getInstance(NetworkObjectSerializer.class));
        kryo.register(TangibleObject.class, injector.getInstance(NetworkObjectSerializer.class));
        kryo.register(PlayerObject.class, injector.getInstance(NetworkObjectSerializer.class));

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
