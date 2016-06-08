package com.ocdsoft.bacta.swg.server.game.script;

import clojure.java.api.Clojure;
import clojure.lang.*;
import clojure.lang.Compiler;
import com.google.common.collect.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.io.FileSystemWatcher;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.swg.conf.ConfigSections;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by crush on 6/6/2016.
 */
@Singleton
public final class ScriptService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptService.class);
    private static final String BACTA_CORE = "bacta/core";

    private final Map<String, ScriptReference> scriptReferences;
    private final Multimap<Class<? extends Event>, IFn> subscribedFunctions;
    private final FileSystemWatcher scriptWatcherService;
    private final Path scriptsPath;

    @Inject
    public ScriptService(final BactaConfiguration configuration) throws Exception {
        this.scriptReferences = new ConcurrentHashMap<>();
        this.scriptsPath = Paths.get(System.getProperty("user.dir"), configuration.getString(ConfigSections.GAME_SERVER_SCRIPTS, "path"));
        this.subscribedFunctions =  Multimaps.synchronizedSetMultimap(HashMultimap.create());
        this.scriptWatcherService = new FileSystemWatcher(scriptsPath, this::onScriptCreated, this::onScriptDeleted, this::onScriptModified);

        initialize();
    }

    /**
     * Subscribes a clojure function to an event type.
     * @param eventType The type of event for which to subscribe.
     * @param scriptFunction The function to be executed when the event is published.
     */
    public void subscribe(final Class<? extends Event> eventType, final IFn scriptFunction) {
        this.subscribedFunctions.put(eventType, scriptFunction);
    }

    /**
     * Removes a clojure function's subscription to an event type.
     * @param eventType The type of event from which to unsubscribe.
     * @param scriptFunction The function which was subscribed.
     */
    public void unsubscribe(final Class<? extends Event> eventType, final IFn scriptFunction) {
        this.subscribedFunctions.remove(eventType, scriptFunction);
    }

    private <T extends Event> void notifySubscribers(final T event) {
        final Collection<IFn> scriptFunctions = this.subscribedFunctions.get(event.getClass());

        if (scriptFunctions != null) {
            for (final IFn scriptFunction : scriptFunctions)
                scriptFunction.invoke(event);
        }
    }

    /**
     * Attaches a script to the specified object.
     *
     * @param scriptName The script to attach.
     * @param object     The object to which to attach the script.
     */
    public void attachScript(final String scriptName, final ServerObject object) {
        //If the script failed to load, then do nothing. Load will output any errors.
        if (!containsScript(scriptName) && !loadScript(scriptName))
            return;

        ensureObjectAttachedScripts(object);

        //Set the script reference on the object.
        final Set<String> attachedScripts = object.getAttachedScripts();
        attachedScripts.add(scriptName);

        final ScriptReference scriptReference = scriptReferences.get(scriptName);
        scriptReference.attachObject(object);
    }

    /**
     * Detaches a script from the specified object.
     *
     * @param scriptName The script to detach.
     * @param object     The object from which to detach the script.
     */
    public void detachScript(final String scriptName, final ServerObject object) {
        if (!containsScript(scriptName))
            return;

        final Set<String> attachedScripts = object.getAttachedScripts();

        if (attachedScripts == null) {
            LOGGER.warn("Attached scripts should not be null, but it is!");
        } else {
            attachedScripts.remove(scriptName);
        }

        final ScriptReference scriptReference = scriptReferences.get(scriptName);
        scriptReference.detachObject(object);

        //TODO: If attached objects == 0 after detaching, we should depersist the script reference, and unload it from memory.
    }

    public <T extends Event> void triggerScript(final String scriptName, final T event) {
        final IFn trigger = Clojure.var(scriptName, "trigger");
        trigger.invoke(event);
    }

    public <T extends Event> void triggerScripts(final T event) {
        notifySubscribers(event);
    }

    public <T extends Event> void triggerScript(final ServerObject serverObject, final String scriptName, final T event) {
        final ScriptReference scriptReference = scriptReferences.get(scriptName);

        //Make sure that the object is attached to the script, and that the script exists.
        if (scriptReference == null || !scriptReference.containsAttachedObject(serverObject)) {
            LOGGER.debug("Tried to trigger script [{}] for object [{}], but was not attached.", scriptName, serverObject.getNetworkId());
            return;
        }

        LOGGER.debug("Triggering script {}", scriptName);

        final IFn trigger = Clojure.var(scriptName, "trigger");
        trigger.invoke(event, serverObject);
    }

    public <T extends Event> void triggerScripts(final ServerObject serverObject, final T event) {
        //invoke all scripts that are attached to this server object and handle the event.
    }

    /**
     * Checks if a specific script exists as a script reference.
     *
     * @param scriptName The script to check.
     * @return True if it exists, otherwise false.
     */
    public boolean containsScript(final String scriptName) {
        return scriptReferences.containsKey(scriptName);
    }

    private boolean loadScript(final String scriptName) {
        try {
            RT.loadResourceScript(scriptName);

            if (!validateScript(scriptName))
                return false;

            //The script is valid. All we need to do is create a new reference for it, and plug it into our map.
            LOGGER.debug("Loading script [{}]", scriptName);

            final ScriptReference scriptReference = new ScriptReference(scriptName);
            scriptReferences.put(scriptName, scriptReference);

            return true;
        } catch (Exception ex) {
            LOGGER.error("Unhandled exception loading script [{}]. Message: {}", scriptName, ex.getMessage());
            return false;
        }
    }

    /**
     * Checks to see if a script is valid syntactically - can it run? Does it violate any custom rules we set?
     *
     * @param scriptName The name to the script.
     * @return True if it is valid. Otherwise, false.
     */
    private boolean validateScript(final String scriptName) {
        //TODO: Some kind of checking to make sure the script will actually run. Clojure lint integration?
        //If it can't run, then output some kind of error messages to the LOGGER explaining the problem with the script.

        return true;
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

        //Should we validate all scripts now, or wait for them to be lazy loaded? I guess it depends on speed.

        //Bind script service in bacta.core namespace.
        RT.loadResourceScript(BACTA_CORE + ".clj");

        final Var scriptService = Var.find(Symbol.intern("bacta.core", "script-service"));
        scriptService.bindRoot(this);
    }

    /**
     * When a script is created, we want to examine the script to see if it has any trigger bindings. If any trigger
     * bindings exist, then we wire those up. If any object has been created that had this script attached, it will
     * already exist in the {@link #scriptReferences} map. If it doesn't exist, then we need to register it there with
     * no attached objects. If it does exist, we don't need to do anything except wire up the reference.
     *
     * @param path The path to the newly created script.
     */
    private void onScriptCreated(final Path path) {
        LOGGER.debug("The script [{}] was created.", path.toString());
    }

    /**
     * When a script is deleted, we simply want to remove the reference to the clojure script, but leave the internal
     * {@link ScriptReference} because it may be recreated later, in which case we want it to be rebound.
     *
     * @param path The path to the script that was deleted.
     */
    private void onScriptDeleted(final Path path) {
        //When a script is deleted, we need to detach it from all objects that were attached to it so that they don't try
        //and call it.
        LOGGER.debug("The script [{}] was deleted.", path.toString());
    }

    /**
     * When a script is modified, it could simply mean that it was renamed, or it could've changed. Either way, we want
     * to update the {@link ScriptReference}.
     *
     * @param path The path to the script that was modified.
     */
    private void onScriptModified(final Path path) {
        LOGGER.debug("The script [{}] was modified.", path.toString());
    }
}
