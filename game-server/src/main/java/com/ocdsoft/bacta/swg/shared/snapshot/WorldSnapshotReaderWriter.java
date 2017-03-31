package com.ocdsoft.bacta.swg.shared.snapshot;

import bacta.iff.Iff;
import com.google.common.collect.ImmutableList;
import com.ocdsoft.bacta.swg.shared.foundation.Crc;
import com.ocdsoft.bacta.swg.shared.foundation.Tag;
import com.ocdsoft.bacta.tre.TreeFile;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntIntMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.ocdsoft.bacta.swg.shared.foundation.Tag.TAG_0000;
import static com.ocdsoft.bacta.swg.shared.foundation.Tag.TAG_0001;

/**
 * Created by crush on 6/3/2016.
 */
public final class WorldSnapshotReaderWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldSnapshotReaderWriter.class);
    private static final String SNAPSHOT_FILENAME_FORMAT = "snapshot/%s.ws";

    private static final int TAG_WSNP = Tag.convertStringToTag("WSNP");
    private static final int TAG_NODS = Tag.convertStringToTag("NODS");
    private static final int TAG_OTNL = Tag.convertStringToTag("OTNL");

    private List<WorldSnapshotNode> nodeList;
    private List<String> objectTemplateNameList;
    private TIntIntMap objectTemplateCrcMap; //Maps from crc to index in name list.
    private TLongObjectMap<WorldSnapshotNode> networkIdToNodeMap; //This map is not thread-safe. Only read from it after loading. Lock when loading.

    private final TreeFile treeFile;

    public WorldSnapshotReaderWriter(final TreeFile treeFile) {
        this.treeFile = treeFile;
    }

    public int getNumberOfNodes() {
        return nodeList != null ? nodeList.size() : 0;
    }

    public WorldSnapshotNode getNode(final int index) {
        return nodeList != null ? nodeList.get(index) : null;
    }

    public int getNumberOfObjectTemplateNames() {
        return objectTemplateNameList != null ? objectTemplateNameList.size() : 0;
    }

    public String getObjectTemplateName(final int index) {
        return objectTemplateNameList != null ? objectTemplateNameList.get(index) : null;
    }

    public int getTotalNumberOfNodes() {
        if (nodeList == null)
            return 0;

        int count = 0;

        for (final WorldSnapshotNode node : nodeList)
            count += node.getTotalNumberOfNodes();

        return count;
    }

    public WorldSnapshotNode getNodeByNetworkId(final long networkId) {
        return networkIdToNodeMap != null ? networkIdToNodeMap.get(networkId) : null;
    }

    public boolean load(final String sceneName) {
        final String fileName = String.format(SNAPSHOT_FILENAME_FORMAT, sceneName);
        final byte[] bytes = treeFile.open(fileName);

        if (bytes == null) {
            LOGGER.error("Unable to load world snapshot file {}", fileName);
            return false;
        }

        load(new Iff(fileName, bytes));

        if (nodeList != null) {
            //Now that all nodes have been created, initialize the networkIdToNodeMap
            networkIdToNodeMap = new TLongObjectHashMap<>(getTotalNumberOfNodes());
            nodeList.forEach(this::mapNodes);
        }

        return true;
    }

    private void load(final Iff iff) {
        if (iff.enterForm(TAG_WSNP, true)) {
            final int version = iff.getCurrentName();

            if (version == TAG_0000) {
                LOGGER.debug("Found version 0000. Not processing.");
            } else if (version == TAG_0001) {
                load0001(iff);
            } else {
                LOGGER.warn("Invalid world snapshot version {}", Tag.convertTagToString(version));
            }

            iff.exitForm(TAG_WSNP);
        }
    }

    private void load0001(final Iff iff) {
        iff.enterForm(TAG_0001);
        {
            loadWorldSnapshotNodes(iff);
            loadObjectTemplateNames(iff);
        }
        iff.exitForm(TAG_0001);
    }

    private void loadWorldSnapshotNodes(final Iff iff) {
        iff.enterForm(TAG_NODS);
        {
            final int count = iff.getNumberOfBlocksLeft();

            if (count > 0) {
                final List<WorldSnapshotNode> localNodeList = new ArrayList<>(count);

                for (int i = 0; i < count; ++i)
                    localNodeList.add(new WorldSnapshotNode(iff));

                this.nodeList = ImmutableList.copyOf(localNodeList);
            }
        }
        iff.exitForm(TAG_NODS);
    }

    private void loadObjectTemplateNames(final Iff iff) {
        iff.enterChunk(TAG_OTNL);
        {
            final int count = iff.readInt();
            final List<String> localObjectTemplateNameList = new ArrayList<>(count);
            final TIntIntMap localObjectTemplateCrcMap = new TIntIntHashMap(count);

            for (int i = 0; i < count; ++i) {
                final String objectTemplateName = iff.readString();
                final int crc = Crc.calculate(objectTemplateName);

                localObjectTemplateCrcMap.put(crc, localObjectTemplateNameList.size());
                localObjectTemplateNameList.add(objectTemplateName);
            }

            this.objectTemplateCrcMap = new TUnmodifiableIntIntMap(localObjectTemplateCrcMap);
            this.objectTemplateNameList = ImmutableList.copyOf(localObjectTemplateNameList);
        }
        iff.exitChunk(TAG_OTNL);
    }

    private void mapNodes(final WorldSnapshotNode node) {
        networkIdToNodeMap.put(node.getNetworkId(), node);
        final List<WorldSnapshotNode> childNodes = node.getNodeList();

        if (childNodes != null)
            childNodes.forEach(this::mapNodes);
    }

    //TODO: Writing methods.
}
