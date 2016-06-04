package com.ocdsoft.bacta.swg.server.game.data.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.object.NetworkObject;
import com.ocdsoft.bacta.swg.shared.object.GameObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kburkhardt on 8/22/14.
 */
@Singleton
public class NetworkObjectSerializer<T extends GameObject> extends Serializer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkObjectSerializer.class);
    private final Map<Class<? extends NetworkObject>, List<Field>> serializableFieldsMap;

    @Inject
    public NetworkObjectSerializer(final Class<? extends GameObject> clazz) {
        serializableFieldsMap = new HashMap<>();
        loadSerializableClass(clazz);
    }

    private List<Field> loadSerializableClass(Class<? extends GameObject> networkObject) {

        List<Field> fields = null;

        if(networkObject.getSuperclass() != null) {
            fields = loadSerializableClass((Class<? extends GameObject>) networkObject.getSuperclass());
        }

        if(fields == null) {
            fields = new ArrayList<>();
        }

        for (Field field : networkObject.getDeclaredFields()) {

            if(!Modifier.isTransient(field.getModifiers()) &&
                !Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                fields.add(field);
            }
        }

        return fields;
    }

    @Override
    public final void write(Kryo kryo, Output output, T object) {

        List<Field> fields = serializableFieldsMap.get(object.getClass());
        for(Field field : fields) {
            try {

                kryo.writeObject(output, field.get(object));

            } catch (IllegalAccessException e) {
                LOGGER.error("Unable to serialize", e);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Offending class: " + object.getClass().getName() + " Field: " + field.getName());
            }
        }
    }

    @Override
    public final T read(Kryo kryo, Input input, Class<T> type) {

        try {

            T newObject = type.newInstance();

            List<Field> fields = serializableFieldsMap.get(type);
            for(Field field : fields) {
                Registration reg = kryo.readClass(input);
                field.set(newObject, kryo.readObject(input, reg.getType()));
            }

            return newObject;

        } catch (IllegalAccessException e) {
            LOGGER.error("Unable to serialize", e);
        } catch (InstantiationException e) {
            LOGGER.error("Unable to instantiate", e);
        }

        return null;
    }
}
