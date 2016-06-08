package com.ocdsoft.bacta.swg.server.game.message;

import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Priority;
import com.ocdsoft.bacta.soe.message.Subscribable;
import com.ocdsoft.bacta.swg.shared.object.ClusterData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 5/21/2016.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public class GameServerOnline extends GameNetworkMessage implements Subscribable {

    private final ClusterData clusterServer;

    public GameServerOnline(final ByteBuffer buffer) {
        clusterServer = new ClusterData(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        clusterServer.writeToBuffer(buffer);
    }
}