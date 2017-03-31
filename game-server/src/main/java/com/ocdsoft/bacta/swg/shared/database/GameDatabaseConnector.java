package com.ocdsoft.bacta.swg.shared.database;

import com.ocdsoft.bacta.engine.object.NetworkObject;

/**
 * Created by kburkhardt on 1/23/15.
 */
public interface GameDatabaseConnector {
    long nextId();
    <T extends NetworkObject> T get(String key);
    <T extends NetworkObject> T get(long key);
    <T extends NetworkObject> void persist(T object);
}
