package com.ocdsoft.bacta.soe.data.couchbase;

import com.google.inject.TypeLiteral;
import com.ocdsoft.bacta.engine.data.ConnectionDatabaseConnector;
import com.ocdsoft.bacta.swg.server.game.data.GameDatabaseConnector;
import com.ocdsoft.bacta.engine.object.NetworkIdGenerator;
import com.ocdsoft.bacta.engine.service.AccountService;
import com.ocdsoft.bacta.swg.server.game.GameModule;
import com.ocdsoft.bacta.swg.server.login.object.SoeAccount;

/**
 * Created by kyle on 6/4/2016.
 */
public class CouchbaseGameModule extends GameModule {
    @Override
    protected void configure() {
        bind(GameDatabaseConnector.class).to(CouchbaseGameDatabaseConnector.class);
        bind(NetworkIdGenerator.class).to(CouchbaseNetworkIdGenerator.class);
        bind(new TypeLiteral<AccountService<SoeAccount>>() {}).to(new TypeLiteral<CouchbaseAccountService<SoeAccount>>() {});
        bind(ConnectionDatabaseConnector.class).to(CouchbaseConnectionDatabaseConnector.class);
    }
}
