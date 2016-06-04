package com.ocdsoft.bacta.swg.server.game.event;

import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.event.Event;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kyle on 6/3/2016.
 */
@AllArgsConstructor
@Getter
public class PlayerOnlineEvent implements Event {
    private final CreatureObject character;
}
