package com.ocdsoft.bacta.swg.server.game.data.serialize;

import com.ocdsoft.bacta.swg.shared.object.GameObject;

/**
 * Created by kyle on 6/4/2016.
 */
public interface GameObjectByteSerializer {
    <T extends GameObject> byte[] serialize(T object);
    <T extends GameObject> T deserialize(byte[] bytes);
}
