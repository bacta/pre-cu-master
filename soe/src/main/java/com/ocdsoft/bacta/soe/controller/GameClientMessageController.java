package com.ocdsoft.bacta.soe.controller;

import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;

/**
 * Created by crush on 5/27/2016.
 */
public interface GameClientMessageController<T extends GameNetworkMessage> {
    void handleIncoming(long[] distributionList, boolean reliable, T message, SoeUdpConnection connection) throws Exception;
}
