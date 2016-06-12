package com.ocdsoft.bacta.swg.shared.database;

import com.ocdsoft.bacta.swg.shared.identity.SoeAccount;
import java.util.Set;

/**
 * Created by kburkhardt on 2/23/14.
 */
public interface ConnectionDatabaseConnector {
    <T> void createObject(String key, T object);
    <T> void updateObject(String key, T object);
    <T> T getObject(String key, Class<T> clazz);
    SoeAccount lookupSession(String authToken);
    Set<String> getClusterCharacterSet(int clusterId);
    int nextClusterId();
    int nextAccountId();
}
