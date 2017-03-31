package com.ocdsoft.bacta.swg.shared.snapshot;

import bacta.iff.Iff;
import bacta.iff.IffWritable;
import com.google.common.collect.ImmutableList;
import com.ocdsoft.bacta.swg.shared.foundation.Tag;
import com.ocdsoft.bacta.swg.shared.math.Quaternion;
import com.ocdsoft.bacta.swg.shared.math.Transform;
import com.ocdsoft.bacta.swg.shared.math.Vector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.ocdsoft.bacta.swg.shared.foundation.Tag.TAG_0000;
import static com.ocdsoft.bacta.swg.shared.foundation.Tag.TAG_DATA;

/**
 * Created by crush on 6/3/2016.
 */
@Getter
@Setter
@AllArgsConstructor
public final class WorldSnapshotNode implements IffWritable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldSnapshotNode.class);
    private static final int TAG_NODE = Tag.convertStringToTag("NODE");

    private final boolean deleted;
    private final long networkId;
    private final long containedByNetworkId;
    private final int objectTemplateNameIndex;
    private final int cellIndex;
    private final Transform transformInParent;
    private final float radius;
    private final int portalLayoutCrc;
    private final WorldSnapshotNode parent;
    private final String eventName;
    private final List<WorldSnapshotNode> nodeList;

    public WorldSnapshotNode(final Iff iff) {
        this(iff, null);
    }

    private WorldSnapshotNode(final Iff iff, final WorldSnapshotNode parent) {
        this.parent = parent;
        this.deleted = false;
        this.eventName = "";

        iff.enterForm(TAG_NODE);
        {
            final int version = iff.getCurrentName();

            if (version == TAG_0000) {
                iff.enterForm(TAG_0000);

                iff.enterChunk(TAG_DATA);
                {
                    this.networkId = iff.readInt();
                    this.containedByNetworkId = iff.readInt();
                    this.objectTemplateNameIndex = iff.readInt();
                    this.cellIndex = iff.readInt();

                    final Quaternion quaternion = new Quaternion(iff);
                    final Vector vector = new Vector(iff);

                    this.transformInParent = new Transform();
                    quaternion.getTransform(transformInParent);
                    transformInParent.setPositionInParentSpace(vector);

                    this.radius = iff.readFloat();
                    this.portalLayoutCrc = iff.readInt();
                }
                iff.exitChunk(TAG_DATA);

                final int childCount = iff.getNumberOfBlocksLeft();

                if (childCount > 0) {
                    final List<WorldSnapshotNode> childNodes = new ArrayList<>(childCount);

                    for (int i = 0; i < childCount; ++i)
                        childNodes.add(new WorldSnapshotNode(iff, this));

                    this.nodeList = ImmutableList.copyOf(childNodes);
                } else {
                    this.nodeList = null;
                }

                iff.exitForm(TAG_0000);
            } else {
                LOGGER.warn("Unknown version {}", Tag.convertTagToString(version));

                this.networkId = 0;
                this.containedByNetworkId = 0;
                this.objectTemplateNameIndex = 0;
                this.cellIndex = 0;
                this.transformInParent = null;
                this.radius = 0.f;
                this.portalLayoutCrc = 0;
                this.nodeList = null;
            }
        }
        iff.exitForm(TAG_NODE);
    }

    public int getTotalNumberOfNodes() {
        int count = 1;

        if (nodeList != null) {
            for (final WorldSnapshotNode node : nodeList)
                count += node.getTotalNumberOfNodes();
        }

        return count;
    }

    @Override
    public void writeToIff(final Iff iff) {
        iff.insertForm(TAG_NODE);
        {
            iff.insertForm(TAG_0000);
            {
                iff.insertChunk(TAG_DATA);
                {
                    iff.insertChunkData(getNetworkId());
                    iff.insertChunkData(getContainedByNetworkId());
                    iff.insertChunkData(getObjectTemplateNameIndex());
                    iff.insertChunkData(getCellIndex());

                    final Quaternion quaternion = new Quaternion(getTransformInParent());
                    quaternion.writeToIff(iff);

                    final Vector vector = getTransformInParent().getPositionInParent();
                    vector.writeToIff(iff);

                    iff.insertChunkData(getRadius());
                    iff.insertChunkData(getPortalLayoutCrc());
                }
                iff.exitChunk(TAG_DATA);

                //Write each child node.
                if (nodeList != null)
                    nodeList.forEach(node -> node.writeToIff(iff));
            }
            iff.exitForm(TAG_0000);
        }
        iff.exitForm(TAG_NODE);
    }
}
