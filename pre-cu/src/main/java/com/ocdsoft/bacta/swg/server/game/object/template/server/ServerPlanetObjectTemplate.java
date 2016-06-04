package com.ocdsoft.bacta.swg.server.game.object.template.server;

import bacta.iff.Iff;
import com.google.common.base.Preconditions;
import com.ocdsoft.bacta.swg.shared.foundation.DataResourceList;
import com.ocdsoft.bacta.swg.shared.foundation.Tag;
import com.ocdsoft.bacta.swg.shared.template.ObjectTemplate;
import com.ocdsoft.bacta.swg.shared.template.definition.TemplateDefinition;
import com.ocdsoft.bacta.swg.shared.utility.StringParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated by the TemplateDefinitionWriter.
 * MANUAL MODIFICATIONS MAY BE OVERWRITTEN.
 */
@TemplateDefinition
public class ServerPlanetObjectTemplate extends ServerUniverseObjectTemplate {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerPlanetObjectTemplate.class);
	public static final int TAG_SERVERPLANETOBJECTTEMPLATE = Tag.convertStringToTag("PLAN");

	private static void registerTemplateConstructors(final DataResourceList<ObjectTemplate> objectTemplateList) {
		objectTemplateList.registerTemplate(ServerPlanetObjectTemplate.TAG_SERVERPLANETOBJECTTEMPLATE, ServerPlanetObjectTemplate::new);
	}

	private int templateVersion;

	private final StringParam planetName = new StringParam(); //name of the planet

	public ServerPlanetObjectTemplate(final String filename, final DataResourceList<ObjectTemplate> objectTemplateList) {
		super(filename, objectTemplateList);
	}

	@Override
	public int getId() {
		return TAG_SERVERPLANETOBJECTTEMPLATE;
	}

	public String getPlanetName() {
		ServerPlanetObjectTemplate base = null;

		if (baseData instanceof ServerPlanetObjectTemplate)
			base = (ServerPlanetObjectTemplate) baseData;

		if (!planetName.isLoaded()) {
			if (base == null) {
				return "";
			} else {
				return base.getPlanetName();
			}
		}

		String value = this.planetName.getValue();
		return value;
	}

	@Override
	protected void load(final Iff iff) {
		if (iff.getCurrentName() != TAG_SERVERPLANETOBJECTTEMPLATE) {
			super.load(iff);
			return;
		}

		iff.enterForm();
		templateVersion = iff.getCurrentName();

		if (templateVersion == Tag.TAG_DERV) {
			iff.enterForm();
			iff.enterChunk();
			final String baseFilename = iff.readString();
			iff.exitChunk();
			final ObjectTemplate base = objectTemplateList.fetch(baseFilename);
			Preconditions.checkNotNull(base, "was unable to load base template %s", baseFilename);
			if (baseData == base && base != null) {
				base.releaseReference();
			} else {
				if (baseData != null)
					baseData.releaseReference();
				baseData = base;
			}
			iff.exitForm();
			templateVersion = iff.getCurrentName();
		}

		iff.enterForm();
		iff.enterChunk();
		final int paramCount = iff.readInt();
		iff.exitChunk();
		for (int i = 0; i < paramCount; ++i) {
			iff.enterChunk();
			final String parameterName = iff.readString();

			if ("planetName".equalsIgnoreCase(parameterName)) {
				planetName.loadFromIff(objectTemplateList, iff);
			} else {
				LOGGER.trace("Unexpected parameter {}", parameterName);
			}

			iff.exitChunk();
		}
		iff.exitForm();

		super.load(iff);
		iff.exitForm();
	}

}

