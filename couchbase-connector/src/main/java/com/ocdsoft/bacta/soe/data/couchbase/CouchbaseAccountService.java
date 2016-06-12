package com.ocdsoft.bacta.soe.data.couchbase;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.swg.shared.database.ConnectionDatabaseConnector;
import com.ocdsoft.bacta.swg.shared.database.AccountService;
import com.ocdsoft.bacta.engine.security.PasswordHash;
import com.ocdsoft.bacta.swg.shared.identity.SoeAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Properties;


/**
 * Created by Kyle on 4/3/14.
 */
@Singleton
public final class CouchbaseAccountService implements AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CouchbaseAccountService.class);
    private final ConnectionDatabaseConnector connector;
    private final Provider<SoeAccount> accountProvider;
    private final PasswordHash passwordHash;
    private final SecureRandom secureRandom;
    private final long authTokenDuration;

    @Inject
    private CouchbaseAccountService(final BactaConfiguration configuration,
                                    final ConnectionDatabaseConnector connector,
                                    final Provider<SoeAccount> accountProvider,
                                    final PasswordHash passwordHash) {

        new Properties().forEach((key, value) -> {

        });
        this.connector = connector;
        this.accountProvider = accountProvider;
        this.passwordHash = passwordHash;
        secureRandom = new SecureRandom();

        authTokenDuration = configuration.getLongWithDefault("Bacta/LoginServer", "AuthTokenTTL", 600) * 1000;
    }


    @Override
    public SoeAccount createAccount(String username, String password) {

        SoeAccount account = accountProvider.get();
        account.setUsername(username);
        try {

            account.setPassword(passwordHash.createHash(password));
            connector.createObject(account.getUsername(), account);
            return account;

        } catch (Exception e) {
            LOGGER.error("Unable to create account", e);
        }
        return null;
    }

    @Override
    public SoeAccount getAccount(final String username) {
        return connector.getObject(username, SoeAccount.class);
    }

    @Override
    public void createAuthToken(final InetAddress address, final SoeAccount account) {

        String authToken = String.valueOf(Math.abs(secureRandom.nextLong())) + String.valueOf(Math.abs(secureRandom.nextLong()));
        account.setAuthToken(authToken);
        account.setAuthExpiration(System.currentTimeMillis() + authTokenDuration);
        account.setAuthInetAddress(address);
        updateAccount(account);
    }

    @Override
    public void updateAccount(final SoeAccount account) {
        connector.updateObject(account.getUsername(), account);
    }

    @Override
    public boolean authenticate(final SoeAccount account, final String password) {
        try {
            return passwordHash.validatePassword(password, account.getPassword());
        } catch (Exception e) {
            LOGGER.error("Unable to authenticate account", e);
        }
        return false;
    }

    @Override
    public SoeAccount validateSession(final InetAddress address, final String authToken) {
        SoeAccount account = connector.lookupSession(authToken);

        if(account != null &&
                account.getAuthExpiration() < System.currentTimeMillis() &&
                account.getAuthInetAddress().equals(address)) {

            account.setAuthToken("");
            account.setAuthExpiration(System.currentTimeMillis());
            account.setAuthInetAddress(null);
            updateAccount(account);

            return null;
        }

        return account;
    }
}
