package com.ocdsoft.bacta.soe.data.couchbase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.swg.shared.database.BactaIdGenerator;
import com.ocdsoft.bacta.swg.shared.database.ConnectionDatabaseConnector;

/**
 * Created by kyle on 6/10/2016.
 */
@Singleton
public class CouchbaseBactaIdGenerator implements BactaIdGenerator {

    private final ConnectionDatabaseConnector databaseConnector;

    @Inject
    public CouchbaseBactaIdGenerator(final ConnectionDatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @Override
    public int next() {
        return databaseConnector.nextAccountId();
    }
}
