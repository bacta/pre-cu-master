package com.ocdsoft.bacta.soe.event;

import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kyle on 6/3/2016.
 */
@AllArgsConstructor
@Getter
public class ConnectEvent implements Event {
    private final SoeUdpConnection connection;
}
