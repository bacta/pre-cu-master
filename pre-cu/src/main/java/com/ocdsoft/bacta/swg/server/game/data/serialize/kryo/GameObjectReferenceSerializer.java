package com.ocdsoft.bacta.swg.server.game.data.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.inject.Inject;
import com.ocdsoft.bacta.engine.service.object.ObjectService;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.shared.object.GameObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kyle on 6/6/2016.
 */
public class GameObjectReferenceSerializer extends Serializer<GameObject> {

    private final Logger LOGGER = LoggerFactory.getLogger(GameObjectReferenceSerializer.class);

    private final ObjectService<ServerObject> serverObjectService;

    @Inject
    public GameObjectReferenceSerializer(final ObjectService<ServerObject> serverObjectService) {
        this.serverObjectService = serverObjectService;
    }


    @Override
    public void write(Kryo kryo, Output output, GameObject object) {

        if(object == null) {
            kryo.writeClassAndObject(output, -1);
        } else {
            kryo.writeClassAndObject(output, object.getNetworkId());
        }
    }

    @Override
    public GameObject read(Kryo kryo, Input input, Class<GameObject> type) {

        final long networkId = kryo.readObject(input, Long.TYPE);
        if (networkId >= 0) {
            return  serverObjectService.get(networkId);
        }
        return null;
    }
}
