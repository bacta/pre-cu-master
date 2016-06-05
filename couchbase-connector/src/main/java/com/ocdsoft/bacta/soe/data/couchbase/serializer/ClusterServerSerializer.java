package com.ocdsoft.bacta.soe.data.couchbase.serializer;

import com.google.gson.*;
import com.ocdsoft.bacta.swg.shared.object.ClusterData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

/**
 * A Gson adapter that handles {@link InetSocketAddress}es.
 */
public class ClusterServerSerializer implements JsonSerializer<ClusterData>, JsonDeserializer<ClusterData> {

    @Override
    public ClusterData deserialize(final JsonElement json, final Type typeOfT,
                                   final JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("not a JSON object");
        }

        final JsonObject obj = (JsonObject) json;

        final int id = obj.get("id").getAsInt();
        final InetSocketAddress remoteAddress = context.deserialize(obj.get("remoteAddress"), InetSocketAddress.class);
        final int tcpPort = obj.get("tcpPort").getAsInt();
        final String serverKey = obj.get("serverKey").getAsString();
        final String name = obj.get("name").getAsString();

        return new ClusterData(id, remoteAddress, tcpPort, serverKey, name);
    }

    @Override
    public JsonElement serialize(final ClusterData src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject obj = new JsonObject();
        obj.addProperty("id", src.getId());
        obj.addProperty("remoteAddress", context.serialize(src.getRemoteAddress(), InetSocketAddress.class).getAsString());
        obj.addProperty("tcpPort", src.getTcpPort());
        obj.addProperty("serverKey", src.getServerKey());
        obj.addProperty("name", src.getName());
        return obj;
    }

}
