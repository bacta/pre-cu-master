package com.ocdsoft.bacta.swg.server.game.script;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.io.FileSystemWatcher;
import com.ocdsoft.bacta.swg.conf.ConfigSections;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
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

    private final Map<String, ScriptReference> scriptReferences;
    private final FileSystemWatcher scriptWatcherService;
    private final Path scriptsPath;

    @Inject
    public ScriptService(final BactaConfiguration configuration) {
        this.scriptReferences = new ConcurrentHashMap<>();
        this.scriptsPath = Paths.get(System.getProperty("user.dir"), configuration.getString(ConfigSections.GAME_SERVER_SCRIPTS, "path"));
        this.scriptWatcherService = new FileSystemWatcher(scriptsPath, this::onScriptCreated, this::onScriptDeleted, this::onScriptModified);

        initialize();
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

        //TODO: See reference counting question in detachScript.
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

        //TODO: Do we want to reference count, and remove the reference from the map when its no longer being used?
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
        if (!validateScript(scriptName))
            return false;

        //The script is valid. All we need to do is create a new reference for it, and plug it into our map.
        LOGGER.debug("Loading script [{}]", scriptName);

        final ScriptReference scriptReference = new ScriptReference(scriptName);
        scriptReferences.put(scriptName, scriptReference);

        return true;
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

    private void initialize() {
        LOGGER.debug("Initializing script service.");

        //Monitor for changes to scripts.
        this.scriptWatcherService.start();

        //Should we validate all scripts now, or wait for them to be lazy loaded? I guess it depends on speed.
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
