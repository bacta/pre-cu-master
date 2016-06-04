package com.ocdsoft.bacta.swg.server.game.player.creation;

import bacta.iff.Iff;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.swg.server.game.service.data.SharedFileLoader;
import com.ocdsoft.bacta.tre.TreeFile;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by crush on 3/29/14.
 */
@Singleton
public class LoadoutEquipment implements SharedFileLoader {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, LoadoutEquipmentInfo> equipmentInfos = new HashMap<>();

    private static final int ID_LOEQ = Iff.createChunkId("LOEQ");
    private static final int ID_0000 = Iff.createChunkId("0000");
    private static final int ID_PTMP = Iff.createChunkId("PTMP");
    private static final int ID_NAME = Iff.createChunkId("NAME");
    private static final int ID_ITEM = Iff.createChunkId("ITEM");

    private final TreeFile treeFile;

    @Inject
    public LoadoutEquipment(final TreeFile treeFile) {
        this.treeFile = treeFile;
        load();
    }

    private void load() {
        logger.trace("Loading default loadout equipment.");

        logger.error("Not implemented.");

        //TODO: Replace with new Iff library.

//        final ChunkReader chunkReader = new ChunkReader("player/default_pc_equipment.iff", treeFile.open("player/default_pc_equipment.iff"));
//
//        ChunkBufferContext root = chunkReader.openChunk();
//
//        if (root == null || !root.isFormType(ID_LOEQ))
//            throw new RuntimeException("Failed to load default loadout equipment. Not a LOEQ file.");
//
//        root = chunkReader.nextChunk();
//
//        if (!root.isFormType(TAG_0000))
//            throw new RuntimeException("Failed to load default loadout equipment. Wrong version.");
//
//        while (root.hasMoreChunks(chunkReader.readerIndex())) {
//            ChunkBufferContext context = chunkReader.nextChunk();
//
//            if (context.isFormType(ID_PTMP)) {
//                LoadoutEquipmentInfo styleInfo = new LoadoutEquipmentInfo(chunkReader);
//                equipmentInfos.put(styleInfo.getPlayerTemplate(), styleInfo);
//            }
//        }
//
//        chunkReader.closeChunk(); //Closes TAG_0000
//        chunkReader.closeChunk(); //Closes ID_HAIR

        logger.debug(String.format("Loaded %d default loadout equipment entries.", equipmentInfos.size()));
    }

    @Override
    public void reload() {
        synchronized (this) {
            equipmentInfos.clear();
            load();
        }
    }

    public static final class LoadoutEquipmentInfo {
        private static final Logger logger = LoggerFactory.getLogger(LoadoutEquipmentInfo.class);

        @Getter
        private String playerTemplate;
        @Getter
        private Collection<String> itemTemplates = new ArrayList<>();

//        public LoadoutEquipmentInfo(ChunkReader reader) {
//            logger.trace("Parsing loadout equipment info.");
//            final ChunkBufferContext root = reader.getCurrentContext();
//
//            while (root.hasMoreChunks(reader.readerIndex())) {
//                ChunkBufferContext context = reader.nextChunk();
//
//                if (context.isChunkId(ID_NAME)) {
//                    playerTemplate = reader.readNullTerminatedAscii();
//                    logger.trace("NAME: " + playerTemplate);
//                } else if (context.isChunkId(ID_ITEM)) {
//                    final int unknownInt = reader.readInt();
//                    final String itemTemplate = reader.readNullTerminatedAscii();
//                    itemTemplates.add(itemTemplate);
//                    logger.trace(String.format("%d:%s", unknownInt, itemTemplate));
//                }
//            }
//        }
    }
}
