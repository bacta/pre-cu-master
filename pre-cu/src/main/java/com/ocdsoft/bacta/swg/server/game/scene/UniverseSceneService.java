package com.ocdsoft.bacta.swg.server.game.scene;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.swg.server.game.object.universe.guild.GuildObject;
import com.ocdsoft.bacta.swg.shared.template.ObjectTemplateList;
import com.ocdsoft.bacta.tre.TreeFile;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by crush on 6/4/2016.
 * <p>
 * Manages all scenes in the universe. If a scene isn't loaded, it will load the scene on request.
 */
@Singleton
public final class UniverseSceneService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniverseSceneService.class);
    private static final String DEFAULT_SCENE_ID = "tatooine";

    private final TreeFile treeFile;
    private final ObjectTemplateList objectTemplateList;

    @Getter
    private GuildObject guildObject; //TODO: How should we set this?

    private final Map<String, Scene> loadedScenes;

    @Inject
    public UniverseSceneService(final TreeFile treeFile,
                                final ObjectTemplateList objectTemplateList) {
        this.treeFile = treeFile;
        this.objectTemplateList = objectTemplateList;
        this.loadedScenes = new ConcurrentHashMap<>(50);
    }

    public Scene getScene(final String sceneId) {
        if (loadedScenes.containsKey(sceneId))
            return loadedScenes.get(sceneId);

        LOGGER.info("Loading scene {}", sceneId);

        final Scene scene = sceneId.startsWith("space_")
                ? new SpaceScene(sceneId)
                : new PlanetScene(sceneId);

        if (!treeFile.exists(scene.getTerrainFileName())) {
            LOGGER.error("Could not find terrain file for scene {}.", sceneId);
            //Load the scene anyways, I guess. We just wanted to know that it had no valid terrain file.
        }

        loadedScenes.put(sceneId, scene);
        return scene;
    }

    public Scene getDefaultScene() {
        return getScene(DEFAULT_SCENE_ID);
    }
}
