package com.ocdsoft.bacta.swg.server.game;

import java.io.*;

import javax.net.ssl.*;
import java.security.Provider;
import java.security.Security;

public final class SecureAuthClient {

    // TODO: Finish
    public SecureAuthClient(final GameServerState gameServerState) {

//        String strServerName = "localhost"; // SSL Server Name
//        int intSSLport = 4443; // Port where the SSL Server is listening
//        PrintWriter out = null;
//        BufferedReader in = null;
//
//        {
//            // Registering the JSSE provider
//            Security.addProvider(new Provider());
//        }
//
//        try {
//            // Creating Client Sockets
//            SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
//            SSLSocket sslSocket = (SSLSocket)sslsocketfactory.createSocket(strServerName,intSSLport);
//
//            // Initializing the streams for Communication with the Server
//            out = new PrintWriter(sslSocket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
//
//            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
//            String userInput = "Hello Testing ";
//            out.println(userInput);
//
//            while ((userInput = stdIn.readLine()) != null) {
//                out.println(userInput);
//                System.out.println("echo: " + in.readLine());
//            }
//
//            out.println(userInput);
//
//            // Closing the Streams and the Socket
//            out.close();
//            in.close();
//            stdIn.close();
//            sslSocket.close();
//        }
//
//        catch(Exception exp)
//        {
//            System.out.println(" Exception occurred .... " +exp);
//            exp.printStackTrace();
//        }

    }

}
