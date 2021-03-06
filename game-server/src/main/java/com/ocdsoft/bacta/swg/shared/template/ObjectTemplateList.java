package com.ocdsoft.bacta.swg.shared.template;

import bacta.iff.Iff;
import com.google.inject.Inject;
import com.ocdsoft.bacta.swg.shared.foundation.ConstCharCrcString;
import com.ocdsoft.bacta.swg.shared.foundation.CrcStringTable;
import com.ocdsoft.bacta.swg.shared.foundation.DataResourceList;
import com.ocdsoft.bacta.tre.TreeFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by crush on 4/20/2016.
 */
public class ObjectTemplateList extends DataResourceList<ObjectTemplate> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectTemplateList.class);

    private static final String defaultCrcStringTable = "misc/object_template_crc_string_table.iff";

    private final CrcStringTable crcStringTable;

    @Inject
    public ObjectTemplateList(final TreeFile treeFile) {
        super(treeFile);
        crcStringTable = new CrcStringTable();
        loadCrcStringTable(defaultCrcStringTable);
    }

    public void loadCrcStringTable(final String filename) {
        final Iff iff = new Iff(filename, treeFile.open(filename));
        crcStringTable.load(iff);
    }

    public <T extends ObjectTemplate> T fetch(final int crc) {
        return super.fetch(lookUp(crc));
    }

    public ConstCharCrcString lookUp(final String string) {
        final ConstCharCrcString result = crcStringTable.lookUp(string);

        if (result.isEmpty())
            LOGGER.warn("objectTemplate {} not found in table", string);

        return result;
    }

    public ConstCharCrcString lookUp(final int crc) {
        return crcStringTable.lookUp(crc);
    }

    public Collection<String> getAllTemplateNamesFromCrcStringTable() {
        return crcStringTable.getAllStrings();
    }
}
