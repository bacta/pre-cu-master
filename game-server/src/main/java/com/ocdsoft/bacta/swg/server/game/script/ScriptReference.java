package com.ocdsoft.bacta.swg.server.game.script;

import com.google.common.collect.ImmutableSet;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by crush on 6/6/2016.
 */
public final class ScriptReference {
    @Getter @Setter
    private String fileName;

    //TODO: Reference to the clojure script.

    private final Set<ServerObject> attachedObjects;

    public ScriptReference(final String fileName) {
        this.fileName = fileName;
        this.attachedObjects = new TreeSet<>();
    }

    //TODO: Set script reference here.
    public final void setScriptReference() {

    }

    public final Set<ServerObject> getAttachedObjects() {
        return ImmutableSet.copyOf(attachedObjects);
    }

    public final void attachObject(final ServerObject serverObject) {
        this.attachedObjects.add(serverObject);
    }

    public final void detachObject(final ServerObject serverObject) {
        this.attachedObjects.remove(serverObject);
    }
}