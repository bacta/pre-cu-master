package com.ocdsoft.bacta.swg.server.game.data.serialize.kryo;

/**
 * Created by kyle on 6/4/2016.
 */
public interface GameObjectSerializer {
    byte[] serialize(Object object);
    Object deserialize(byte[] bytes);
}
