/*
 * Created by IntelliJ IDEA.
 * User: Kyle
 * Date: 4/3/14
 * Time: 8:50 PM
 */
package com.ocdsoft.bacta.swg.shared.database;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ocdsoft.bacta.swg.shared.identity.SoeAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public final class AccountProvider implements Provider<SoeAccount> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountProvider.class);

    private final BactaIdGenerator idGenerator;
    private final Constructor<SoeAccount> accountConstructor;

    @Inject
    public AccountProvider(final BactaIdGenerator idGenerator, final SoeAccount account) throws NoSuchMethodException {
        this.idGenerator = idGenerator;
        accountConstructor = (Constructor<SoeAccount>) account.getClass().getConstructor(Integer.TYPE);
    }

    @Override
    public SoeAccount get() {
        try {
            return accountConstructor.newInstance(idGenerator.next());
        } catch (Exception e) {
            LOGGER.error("Unable to create account object", e);
        }

        return null;
    }
}
