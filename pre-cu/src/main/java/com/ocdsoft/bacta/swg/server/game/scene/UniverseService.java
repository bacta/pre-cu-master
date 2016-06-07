package com.ocdsoft.bacta.swg.server.game.scene;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.swg.conf.ConfigSections;
import com.ocdsoft.bacta.swg.server.game.object.universe.guild.GuildObject;
import com.ocdsoft.bacta.swg.shared.template.ObjectTemplateList;
import com.ocdsoft.bacta.tre.TreeFile;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by crush on 6/4/2016.
 * <p>
 * Manages all scenes in the universe. If a scene isn't loaded, it will load the scene on request.
 */
@Singleton
public final class UniverseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniverseService.class);
    private static final String TERRAIN_PATH_FORMAT = "terrain/%s.trn";
    private static final String DEFAULT_SCENE_ID = "tatooine";

    private static final int DEFAULT_WEATHER_UPDATE_INTERVAL = 900;

    private final TreeFile treeFile;
    private final ObjectTemplateList objectTemplateList;

    private final Set<String> configuredScenes;
    private final Map<String, Scene> loadedScenes;

    //Should we do this on a per scene basis?
    private final int weatherUpdateInterval;

    @Getter
    private GuildObject guildObject;

    @Inject
    public UniverseService(final TreeFile treeFile,
                           final ObjectTemplateList objectTemplateList,
                           final BactaConfiguration bactaConfiguration) {
        this.treeFile = treeFile;
        this.objectTemplateList = objectTemplateList;
        this.loadedScenes = new ConcurrentHashMap<>(50);

        this.weatherUpdateInterval = bactaConfiguration.getIntWithDefault(
                ConfigSections.GAME_SERVER_UNIVERSE, "weatherUpdateInterval", DEFAULT_WEATHER_UPDATE_INTERVAL);

        final Collection<String> scenes = bactaConfiguration.getStringCollection(ConfigSections.GAME_SERVER_SCENES, "scene");
        this.configuredScenes = ImmutableSet.copyOf(scenes);

        loadConfiguredScenes();
        //loadGuildObject();
    }

    private void loadConfiguredScenes() {
        if (configuredScenes.size() > 0) {
            configuredScenes.forEach(this::getScene);
        } else {
            //If no scenes have been configured, then try to load the default scene.
            getScene(DEFAULT_SCENE_ID);
        }
    }

    private Scene loadSceneFromDatabase(final String sceneId) {
        LOGGER.debug("Loading scene from database not implemented.");
        return null;
    }

    private void saveSceneToDatabase(final Scene scene) {
        LOGGER.warn("Writing scene to database not implemented.");
    }

    private Scene createScene(final String sceneId) {
        LOGGER.debug("Creating new scene {} and serializing it in the database.", sceneId);

        //Validate that the terrain file exists and is accessible by the server.
        final String terrainFileName = String.format(TERRAIN_PATH_FORMAT, sceneId);

        if (!treeFile.exists(terrainFileName)) {
            LOGGER.error("Could not find the terrain file for scene {}. Aborted creation of scene.", sceneId);
            return null;
        }

        final SceneType sceneType = sceneId.startsWith("space_") ? SceneType.SPACE : SceneType.GROUND;

        final Scene scene = new Scene(sceneId, terrainFileName, sceneType, weatherUpdateInterval);
        saveSceneToDatabase(scene);

        return scene;
    }

    public Scene getScene(final String sceneId) {
        if (loadedScenes.containsKey(sceneId))
            return loadedScenes.get(sceneId);

        Scene scene = loadSceneFromDatabase(sceneId);

        //If loading from database failed, then it needs to be created for the first time.
        if (scene == null)
            scene = createScene(sceneId);

        //If we successfully created the scene, put it into the loaded scenes map.
        if (scene != null)
            loadedScenes.put(sceneId, scene);

        return scene;
    }

    /**
     * Loads the default scene for the server. If it is not loaded, it will be created. The default scene is
     * currently specified as tatooine.
     * @return The default scene.
     */
    public Scene getDefaultScene() {
        return getScene(DEFAULT_SCENE_ID);
    }
}
