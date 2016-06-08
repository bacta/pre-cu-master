package com.ocdsoft.bacta.swg.server.game.data;

import com.ocdsoft.bacta.swg.shared.object.GameObject;

/**
 * Created by kburkhardt on 1/23/15.
 */
public interface GameDatabaseConnector {

    <T extends GameObject> T get(String key);

    <T extends GameObject> T get(long key);

    <T extends GameObject> void persist(T object);

    long nextId();
}
