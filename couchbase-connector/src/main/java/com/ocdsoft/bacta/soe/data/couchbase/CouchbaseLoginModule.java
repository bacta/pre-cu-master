package com.ocdsoft.bacta.soe.data.couchbase;

import com.ocdsoft.bacta.swg.shared.database.AccountService;
import com.ocdsoft.bacta.swg.shared.database.ConnectionDatabaseConnector;
import com.ocdsoft.bacta.swg.server.login.LoginModule;

/**
 * Created by kyle on 6/4/2016.
 */
public class CouchbaseLoginModule extends LoginModule {
    @Override
    protected void configure() {
        bind(ConnectionDatabaseConnector.class).to(CouchbaseConnectionDatabaseConnector.class);
        bind(AccountService.class).to(CouchbaseAccountService.class);
    }
}
