package com.ocdsoft.bacta.swg.server;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.ocdsoft.bacta.swg.server.chat.BaseChatModule;
import com.ocdsoft.bacta.swg.server.chat.ChatModule;
import com.ocdsoft.bacta.swg.server.chat.ChatServer;
import com.ocdsoft.bacta.swg.server.game.GameModule;
import com.ocdsoft.bacta.swg.server.game.GameServer;
import com.ocdsoft.bacta.swg.server.login.LoginModule;
import com.ocdsoft.bacta.swg.server.login.LoginServer;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by kburkhardt on 12/29/14.
 */
public final class PreCuServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreCuServer.class);

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

        Set<String> argSet = new HashSet<>();
        for(String arg : args) {
            argSet.add(arg.toLowerCase());
        }

        if(argSet.contains("login")) {
            LOGGER.info("Starting LoginServer");
            Injector injector = Guice.createInjector(getModules(LoginModule.class));
            LoginServer loginServer = injector.getInstance(LoginServer.class);
            Thread loginThread = new Thread(loginServer);
            loginThread.start();
        }

        if(argSet.contains("game")) {
            LOGGER.info("Starting GameServer");

            Injector injector = Guice.createInjector(getModules(GameModule.class));
            GameServer gameServer = injector.getInstance(GameServer.class);
            Thread gameThread = new Thread(gameServer);
            gameThread.start();
        }

        if (argSet.contains("chat")) {
            LOGGER.info("Starting ChatServer");
            Injector injector = Guice.createInjector(getModules(ChatModule.class));
            ChatServer chatServer = injector.getInstance(ChatServer.class);
            Thread chatThread = new Thread(chatServer);
            chatThread.start();
        }

    }

    private static <T> List<Module> getModules(Class<T> moduleClass) throws InstantiationException, IllegalAccessException {

        Reflections reflections = new Reflections();

        List<Module> moduleList = new ArrayList<>();
        moduleList.add(new PreCuModule());

        Set<Class<? extends T>> modules = reflections.getSubTypesOf(moduleClass);
        for(Class<? extends T> module : modules) {
            moduleList.add((Module) module.newInstance());
        }
        return moduleList;
    }
}
