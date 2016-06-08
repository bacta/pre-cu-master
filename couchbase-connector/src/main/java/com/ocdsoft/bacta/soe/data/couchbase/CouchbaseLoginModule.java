package com.ocdsoft.bacta.soe.data.couchbase;

import com.google.inject.TypeLiteral;
import com.ocdsoft.bacta.engine.data.ConnectionDatabaseConnector;
import com.ocdsoft.bacta.engine.service.AccountService;
import com.ocdsoft.bacta.swg.server.login.LoginModule;
import com.ocdsoft.bacta.swg.server.login.object.SoeAccount;

/**
 * Created by kyle on 6/4/2016.
 */
public class CouchbaseLoginModule extends LoginModule {
    @Override
    protected void configure() {
        bind(ConnectionDatabaseConnector.class).to(CouchbaseConnectionDatabaseConnector.class);
        bind(new TypeLiteral<AccountService<SoeAccount>>() {}).to(new TypeLiteral<CouchbaseAccountService<SoeAccount>>() {});
    }
}
