package com.ocdsoft.bacta.swg.shared.buildout;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.tre.TreeFile;

/**
 * Created by crush on 6/3/2016.
 */
@Singleton
public final class BuildoutLoader {
    private static final String BUILDOUT_FILE_FORMAT = "datatables/buildout/%s/%s.iff";
    private static final int SHARED_CELL_OBJECT_TYPE = 0x0C5401EE;

    private final TreeFile treeFile;

    @Inject
    public BuildoutLoader(final TreeFile treeFile) {
        this.treeFile = treeFile;
    }

    /**
     * Loads the buildout information for the given scene.
     *
     * @param sceneName The scene for which to load the buildout.
     */
    public void loadBuildout(final String sceneName) {
    }
}
