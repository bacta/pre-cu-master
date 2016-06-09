package com.ocdsoft.bacta.swg.server.game.script;

import clojure.lang.IFn;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by crush on 6/6/2016.
 *
 * Encapsulates objects that have attached themselves to this script. Also encapsulates all the triggers that this
 * particular script has available.
 */
public final class ScriptReference {
    private String scriptFilePath;
    private final Set<ServerObject> attachedObjects;
    private final Multimap<Class<? extends Event>, IFn> eventHandlers;


    public ScriptReference(final String scriptFilePath) {
        this.scriptFilePath = scriptFilePath;
        this.attachedObjects = new TreeSet<>();
        this.eventHandlers = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    }

    public final Set<ServerObject> getAttachedObjects() {
        return ImmutableSet.copyOf(attachedObjects);
    }

    public void attachObject(final ServerObject serverObject) {
        this.attachedObjects.add(serverObject);
    }

    public void detachObject(final ServerObject serverObject) {
        this.attachedObjects.remove(serverObject);
    }

    public boolean containsAttachedObject(final ServerObject serverObject) {
        return this.attachedObjects.contains(serverObject);
    }

    /**
     * Adds an event handler to this script reference's list of handlers.
     * @param eventType The type of event being handled.
     * @param eventHandler Reference to the clojure function that will handle the event.
     */
    public void addEventHandler(final Class<? extends Event> eventType, final IFn eventHandler) {
        eventHandlers.put(eventType, eventHandler);
    }

    /**
     * Removes an event handler from this script reference's list of handlers.
     * @param eventType The type of event that was being handled.
     * @param eventHandler Reference to the clojure function that was handling the event.
     */
    public void removeEventHandler(final Class<? extends Event> eventType, final IFn eventHandler) {
        eventHandlers.remove(eventType, eventHandler);
    }

    /**
     * Clears all the event handlers for this script reference. Useful when reloading a script.
     */
    public void clearEventHandlers() {
        eventHandlers.clear();
    }
}