package com.ocdsoft.bacta.swg.server.login.message;


import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Priority;
import com.ocdsoft.bacta.swg.shared.identity.CharacterInfo;
import com.ocdsoft.bacta.swg.shared.identity.SoeAccount;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

@Priority(0x2)
public final class EnumerateCharacterId extends GameNetworkMessage {

    private final Set<CharacterInfo> characterInfoList;

    private EnumerateCharacterId() {
        characterInfoList = new TreeSet<>();
    }

    public EnumerateCharacterId(final SoeAccount account) {
        this();
        characterInfoList.addAll(account.getCharacterList());
	}

    public EnumerateCharacterId(final ByteBuffer buffer) {
        this();
        for(int i = 0; i < buffer.getInt(); ++i) {
            CharacterInfo info = new CharacterInfo(buffer);
            characterInfoList.add(info);
        }
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {

        buffer.putInt(characterInfoList.size());
        for(CharacterInfo info : characterInfoList) {
            info.writeToBuffer(buffer);
        }
    }
}
