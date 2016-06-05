package com.ocdsoft.bacta.swg.server.game.data.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ocdsoft.bacta.swg.server.game.object.ServerObjectConstructorMap;
import com.ocdsoft.bacta.swg.server.game.object.template.server.ServerObjectTemplate;
import com.ocdsoft.bacta.swg.server.game.service.data.ObjectTemplateService;
import com.ocdsoft.bacta.swg.shared.container.SlotIdManager;
import com.ocdsoft.bacta.swg.shared.object.GameObject;
import com.ocdsoft.bacta.swg.shared.template.ObjectTemplateList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kburkhardt on 8/22/14.
 */
public class NetworkObjectSerializer<T extends GameObject> extends Serializer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkObjectSerializer.class);
    private static final Map<Class<? extends GameObject>, List<Field>> serializableFieldsMap;

    private final ObjectTemplateList objectTemplateList;
    private final SlotIdManager slotIdManager;

    private final ServerObjectConstructorMap constructorMap;
    private final ObjectTemplateService objectTemplateService;

    static {
        serializableFieldsMap = new ConcurrentHashMap<>();
    }

    public NetworkObjectSerializer(final Class<? extends GameObject> clazz,
                                   final ObjectTemplateList objectTemplateList,
                                   final SlotIdManager slotIdManager,
                                   final ServerObjectConstructorMap constructorMap,
                                   final ObjectTemplateService objectTemplateService) {

        this.objectTemplateList = objectTemplateList;
        this.slotIdManager = slotIdManager;
        this.constructorMap = constructorMap;
        this.objectTemplateService = objectTemplateService;

        loadSerializableClass(clazz);
    }

    private void loadSerializableClass(Class<? extends GameObject> networkObject) {

        if(networkObject.getSuperclass() != Object.class) {
            loadSerializableClass((Class<? extends GameObject>) networkObject.getSuperclass());
        }

        List<Field> fields = serializableFieldsMap.get(networkObject);

        if(fields != null) {
            return;
        }

        fields = new LinkedList<>();


        for (Field field : networkObject.getDeclaredFields()) {

            if(!Modifier.isTransient(field.getModifiers()) &&
                !Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                fields.add(field);
            }
        }

        verifyDataVersions(networkObject, fields);
        serializableFieldsMap.put(networkObject, fields);
    }

    private void verifyDataVersions(final Class<? extends GameObject> networkObject, final List<Field> fields) {



    }

    @Override
    public final void write(Kryo kryo, Output output, T object) {

        String templateName = object.getObjectTemplate().getResourceName();
        kryo.writeObject(output, templateName);

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

        String templatePath = kryo.readObject(input, String.class);
        ServerObjectTemplate serverObjectTemplate = objectTemplateService.getObjectTemplate(templatePath);

        final Constructor<T> constructor = constructorMap.get(type);

        try {

            T newObject = constructor.newInstance(objectTemplateList, slotIdManager, serverObjectTemplate);

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
        } catch (InvocationTargetException e) {
            LOGGER.error("Unable to Invocate", e);
        }

        return null;
    }
}
