package com.ocdsoft.bacta.swg.server.game.script;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.io.FileSystemWatcher;
import com.ocdsoft.bacta.engine.io.FileSystemWatcherEvent;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.soe.service.PublisherService;
import com.ocdsoft.bacta.swg.conf.ConfigSections;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by crush on 6/6/2016.
 */
@Singleton
public final class ScriptService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptService.class);
    private static final String BACTA_CORE = "bacta.core";

    //Symbols
    private static final Symbol BACTA_CORE_NS = Symbol.intern(BACTA_CORE);
    //Service Symbols
    private static final Symbol SCRIPT_SERVICE = Symbol.intern(BACTA_CORE, "script-service");
    private static final Symbol PUBLISHER_SERVICE = Symbol.intern(BACTA_CORE, "publisher-service");
    //Api Symbols
    private static final Symbol TRACK_SCRIPT = Symbol.intern(BACTA_CORE, "track-script");
    private static final Symbol ATTACH_SCRIPT = Symbol.intern(BACTA_CORE, "attach-script");
    private static final Symbol DETACH_SCRIPT = Symbol.intern(BACTA_CORE, "detach-script");
    private static final Symbol TRIGGER_SCRIPT = Symbol.intern(BACTA_CORE, "trigger-script");
    private static final Symbol TRIGGER_SCRIPTS = Symbol.intern(BACTA_CORE, "trigger-scripts");

    private final PublisherService publisherService;
    private final FileSystemWatcher scriptWatcherService;
    private final Path scriptsPath;

    private IFn trackScript;
    private IFn attachScript;
    private IFn detachScript;
    private IFn triggerScript;
    private IFn triggerScripts;

    @Inject
    public ScriptService(final BactaConfiguration configuration,
                         final PublisherService publisherService) throws Exception {
        this.publisherService = publisherService;
        this.scriptsPath = Paths.get(System.getProperty("user.dir"), configuration.getString(ConfigSections.GAME_SERVER_SCRIPTS, "path"));
        this.scriptWatcherService = new FileSystemWatcher(scriptsPath, null, null, null, this::onScriptChanged);

        initialize();
    }

    /**
     * Attaches a script to the specified object.
     *
     * @param scriptNamespace The namespace of the script to attach.
     * @param object          The object to which to attach the script.
     */
    public void attachScript(final String scriptNamespace, final ServerObject object) {
        ensureObjectAttachedScripts(object);
        attachScript.invoke(scriptNamespace, object);
    }

    /**
     * Detaches a script from the specified object.
     *
     * @param scriptNamespace The namespace of the script to detach.
     * @param object          The object from which to detach the script.
     */
    public void detachScript(final String scriptNamespace, final ServerObject object) {
        detachScript.invoke(scriptNamespace, object);
    }

    public <T extends Event> void triggerScript(final String scriptName, final T event) {
        triggerScript.invoke(event, scriptName);
    }

    public <T extends Event> void triggerScript(final ServerObject serverObject, final String scriptName, final T event) {
        triggerScript.invoke(event, scriptName, serverObject);
    }

    public <T extends Event> void triggerScripts(final T event) {
        triggerScripts.invoke(event);
    }

    public <T extends Event> void triggerScripts(final ServerObject serverObject, final T event) {
        triggerScripts.invoke(event, serverObject);
    }

    private void ensureObjectAttachedScripts(final ServerObject object) {
        final Set<String> attachedScripts = object.getAttachedScripts();

        if (attachedScripts == null)
            object.setAttachedScripts(new HashSet<>());
    }

    private void initialize() throws Exception {
        LOGGER.debug("Initializing script service.");

        //Monitor for changes to scripts.
        this.scriptWatcherService.start();

        RT.loadResourceScript("bacta/core.clj");

        final Var scriptService = Var.find(SCRIPT_SERVICE);
        scriptService.bindRoot(this);

        final Var publisherService = Var.find(PUBLISHER_SERVICE);
        publisherService.bindRoot(publisherService);

        //Cache these core functions that are part of our API with ScriptService.
        trackScript = Clojure.var(TRACK_SCRIPT);
        attachScript = Clojure.var(ATTACH_SCRIPT);
        detachScript = Clojure.var(DETACH_SCRIPT);
        triggerScript = Clojure.var(TRIGGER_SCRIPT);
        triggerScripts = Clojure.var(TRIGGER_SCRIPTS);
    }

    /**
     * If a script that the script service file system watcher is watching changes, then this method
     * is notified.
     *
     * @param event The event object.
     */
    private void onScriptChanged(final FileSystemWatcherEvent event) {
        LOGGER.debug("Script {} {}", event.getFilePath(), event.getEventType());
        trackScript.invoke(event);
    }
}
