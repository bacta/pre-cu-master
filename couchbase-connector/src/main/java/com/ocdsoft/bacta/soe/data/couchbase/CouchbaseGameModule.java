package com.ocdsoft.bacta.soe.data.couchbase;

import com.ocdsoft.bacta.swg.shared.database.AccountService;
import com.ocdsoft.bacta.swg.shared.database.ConnectionDatabaseConnector;
import com.ocdsoft.bacta.swg.shared.database.GameDatabaseConnector;
import com.ocdsoft.bacta.swg.shared.database.NetworkIdGenerator;
import com.ocdsoft.bacta.swg.server.game.GameModule;

/**
 * Created by kyle on 6/4/2016.
 */
public class CouchbaseGameModule extends GameModule {
    @Override
    protected void configure() {
        bind(GameDatabaseConnector.class).to(CouchbaseGameDatabaseConnector.class);
        bind(NetworkIdGenerator.class).to(CouchbaseNetworkIdGenerator.class);
        bind(AccountService.class).to(CouchbaseAccountService.class);
        bind(ConnectionDatabaseConnector.class).to(CouchbaseConnectionDatabaseConnector.class);
    }
}
