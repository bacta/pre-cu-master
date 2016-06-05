package com.ocdsoft.bacta.swg.server.game;

import com.ocdsoft.bacta.soe.ServerState;
import com.ocdsoft.bacta.swg.shared.object.ClusterData;

/**
 * Created by kyle on 4/11/2016.
 */

public interface GameServerState extends ServerState {
    int getClusterId();
    ClusterData getClusterServer();
    void setOnlineUsers(int onlineUsers);
    String getBranch();
    int getVersion();
    String getNetworkVersion();
}
