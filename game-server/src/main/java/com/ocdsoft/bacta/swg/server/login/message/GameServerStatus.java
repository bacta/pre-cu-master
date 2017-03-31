package com.ocdsoft.bacta.swg.server.login.message;

import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Priority;
import com.ocdsoft.bacta.soe.message.Subscribable;
import com.ocdsoft.bacta.swg.shared.object.ClusterData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@AllArgsConstructor
@Getter
@Priority(0x4)
public final class GameServerStatus extends GameNetworkMessage implements Subscribable {

    private final ClusterData clusterServer;

    public GameServerStatus(final ByteBuffer buffer) {
        this.clusterServer = new ClusterData(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        clusterServer.writeToBuffer(buffer);
    }

}
