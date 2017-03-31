package com.ocdsoft.bacta.swg.shared.database;

import com.ocdsoft.bacta.swg.shared.identity.SoeAccount;

import java.net.InetAddress;

public interface AccountService {

	boolean authenticate(SoeAccount account, String password);

	SoeAccount validateSession(InetAddress address, String authToken);

    SoeAccount createAccount(String username, String password);

    SoeAccount getAccount(String username);

    void createAuthToken(InetAddress address, SoeAccount account);

    void updateAccount(SoeAccount account);
}
