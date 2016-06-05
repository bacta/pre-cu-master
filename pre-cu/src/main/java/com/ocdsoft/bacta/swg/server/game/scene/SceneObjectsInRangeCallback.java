package com.ocdsoft.bacta.swg.server.game.scene;

import com.google.common.collect.ImmutableSet;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObject;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec3;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by crush on 6/4/2016.
 */
public final class SceneObjectsInRangeCallback implements SearchCallback<Vec3> {
    private final Set<ServerObject> inRangeObjects = new TreeSet<>();

    @Override
    public boolean onFound(final SpatialEntity<Vec3> entity, final float v, final int i, final Vec3 v1, final float v2, final int i1, final long l) {
        inRangeObjects.add((TangibleObject) entity);
        return true;
    }

    public Set<ServerObject> getInRangeObjects() {
        return ImmutableSet.copyOf(inRangeObjects);
    }
}
