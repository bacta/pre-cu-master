package com.ocdsoft.bacta.swg.server.game.script;

import clojure.lang.IFn;
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
    @Getter
    @Setter
    private String fileName;
    private final Set<ServerObject> attachedObjects;

    public ScriptReference(final String fileName) {
        this.fileName = fileName;
        this.attachedObjects = new TreeSet<>();
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
}