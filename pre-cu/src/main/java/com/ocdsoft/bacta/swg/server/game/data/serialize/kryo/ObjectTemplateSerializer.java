package com.ocdsoft.bacta.swg.server.game.data.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ocdsoft.bacta.engine.service.object.ObjectService;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.template.server.ServerObjectTemplate;
import com.ocdsoft.bacta.swg.server.game.service.data.ObjectTemplateService;
import com.ocdsoft.bacta.swg.shared.object.GameObject;
import com.ocdsoft.bacta.swg.shared.template.ObjectTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kyle on 6/6/2016.
 */
public class ObjectTemplateSerializer extends Serializer<ObjectTemplate> {

    private final Logger LOGGER = LoggerFactory.getLogger(ObjectTemplateSerializer.class);

    private final ObjectTemplateService objectTemplateService;

    public ObjectTemplateSerializer(final ObjectTemplateService objectTemplateService) {
        this.objectTemplateService = objectTemplateService;
    }

    @Override
    public void write(Kryo kryo, Output output, ObjectTemplate object) {
        String templateName = object.getResourceName();
        kryo.writeObject(output, templateName);
        LOGGER.trace("Writing Template: {}", templateName);
    }

    @Override
    public ObjectTemplate read(Kryo kryo, Input input, Class<ObjectTemplate> type) {
        String templatePath = kryo.readObject(input, String.class);
        LOGGER.trace("Found Template {}", templatePath);
        return objectTemplateService.getObjectTemplate(templatePath);
    }
}
