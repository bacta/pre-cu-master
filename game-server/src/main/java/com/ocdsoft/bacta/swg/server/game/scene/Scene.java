package com.ocdsoft.bacta.swg.server.game.scene;

import com.ocdsoft.bacta.swg.server.game.message.scene.SceneDestroyObject;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObject;
import com.ocdsoft.bacta.swg.shared.math.Vector;
import com.ocdsoft.bacta.swg.shared.utility.WeatherGenerator;
import lombok.Getter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.quad.SpatialQuadTree;
import org.magnos.steer.vec.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by crush on 6/4/2016.
 * <p>
 * Represents a single Scene. Equivalent to ServerWorld.
 */
public final class Scene {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scene.class);

    private static final Vec3 EXTENT_MIN = new Vec3(-8192, -1000, -8192);
    private static final Vec3 EXTENT_MAX = new Vec3(-8192, 1000, 8192);
    private static final int DESIRED_LEAF_SIZE = 16;
    private static final int REFRESH_THRESHOLD = 10;
    private static final int MAX_ENTITIES_SEARCH = 512;

    private final SpatialDatabase<Vec3> spatialDatabase;
    @Getter
    private final String sceneId;
    @Getter
    private final String terrainFileName;
    @Getter
    private final SceneType sceneType;

    private final WeatherGenerator weatherGenerator;

    public Scene(final String sceneId, final String terrainFileName, final SceneType sceneType, final int weatherUpdateInterval) {
        this.spatialDatabase = new SpatialQuadTree<>(EXTENT_MIN, EXTENT_MAX, DESIRED_LEAF_SIZE, REFRESH_THRESHOLD);
        this.sceneType = sceneType;
        this.sceneId = sceneId;
        this.terrainFileName = terrainFileName;
        this.weatherGenerator = new WeatherGenerator(weatherUpdateInterval);
    }

//
//
//
//    @Override
//    public void add(TangibleObject obj) {
//
//        Zone currentZone = obj.getZone();
//        if (currentZone != null) {
//            currentZone.remove(obj);
//        }
//
//        if(obj.getZone() == this) {
//            return;
//        }
//
//        obj.setZone(this);
//        obj.setPosition(obj.getTransformObjectToParent(), false);
//        obj.setInert(false);
//
//        spatialDatabase.add(obj);
//        int count = spatialDatabase.refresh();
//        logger.debug("Add " + obj.getNetworkId() + " to "  + terrainName + " Now has " + count + " objects");
//        obj.updateZone();
//
//        planetMap.addObject(this, obj);
//    }
//
//    @Override
//    public void remove(TangibleObject obj) {
//        obj.broadcastMessage(new SceneDestroyObject(obj.getNetworkId(), false));
//        obj.setInert(true);
//        int count = spatialDatabase.refresh();
//        logger.debug("Remove " + obj.getNetworkId() + " to "  + terrainName + " Now has " + count + " objects");
//        obj.clearZone();
//        planetMap.removeObject(this, obj);
//    }

    public void add(final ServerObject obj) {
        //flag the obj as in world.

        final TangibleObject tangibleObject = obj.asTangibleObject();

        if (tangibleObject != null) {
            this.spatialDatabase.add(tangibleObject);
            final int count = this.spatialDatabase.refresh();

            LOGGER.debug("Added {} to the spatial database. Now tracking {} objects.",
                    tangibleObject.getDebugInformation(),
                    count);
        }
    }

    public void remove(final ServerObject obj) {
        //flag the obj as not in world.

        final TangibleObject tangibleObject = obj.asTangibleObject();

        if (tangibleObject != null) {
            tangibleObject.setInert(true);
            final int count = this.spatialDatabase.refresh();

            LOGGER.debug("Removed {} from the spatial database. Now tracking {} objects.",
                    tangibleObject.getDebugInformation(),
                    count);
        }
    }

    public Set<ServerObject> getInRangeObjects(final Vector location, final float distance) {
        final Vec3 vec3 = location.asVec3();

        final SceneObjectsInRangeCallback callback = new SceneObjectsInRangeCallback();
        this.spatialDatabase.contains(vec3, distance, MAX_ENTITIES_SEARCH, SpatialDatabase.ALL_GROUPS, callback);

        return callback.getInRangeObjects();
    }
}
