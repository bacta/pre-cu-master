package com.ocdsoft.bacta.soe.data.couchbase;

import com.ocdsoft.bacta.engine.data.GameDatabaseConnector;
import com.ocdsoft.bacta.engine.object.NetworkIdGenerator;
import com.ocdsoft.bacta.swg.server.game.GameModule;

/**
 * Created by kyle on 6/4/2016.
 */
public class CouchbaseGameModule extends GameModule {
    @Override
    protected void configure() {
        bind(GameDatabaseConnector.class).to(CouchbaseGameDatabaseConnector.class);
        bind(NetworkIdGenerator.class).to(CouchbaseNetworkIdGenerator.class);
    }
}
