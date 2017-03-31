package com.ocdsoft.bacta.swg.server.game.message;

import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.soe.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.message.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by crush on 6/5/2016.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public final class AttributeListMessage extends GameNetworkMessage {
    private final long networkId;
    private final String staticItemName;
    private final List<AttributeKeyValuePair> data;
    private final int revision;

    public AttributeListMessage(final ByteBuffer buffer) {
        networkId = buffer.getLong();
        staticItemName = BufferUtil.getAscii(buffer);
        data = BufferUtil.getArrayList(buffer, AttributeKeyValuePair::new);
        revision = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, networkId);
        BufferUtil.putAscii(buffer, staticItemName);
        BufferUtil.put(buffer, data);
        BufferUtil.put(buffer, revision);
    }

    @Getter
    @AllArgsConstructor
    public static final class AttributeKeyValuePair implements ByteBufferWritable{
        private final String key;
        private final String value; //unicode

        public AttributeKeyValuePair(final ByteBuffer buffer) {
            this.key = BufferUtil.getAscii(buffer);
            this.value = BufferUtil.getUnicode(buffer);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final AttributeKeyValuePair that = (AttributeKeyValuePair) o;

            if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) return false;
            return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;

        }

        @Override
        public int hashCode() {
            int result = getKey() != null ? getKey().hashCode() : 0;
            result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
            return result;
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            BufferUtil.putAscii(buffer, key);
            BufferUtil.putUnicode(buffer, value);
        }
    }
}