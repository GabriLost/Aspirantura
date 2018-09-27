package ru.sbertech.atlas.jira.cupintegration.in.utils;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;
import ru.sbertech.atlas.jira.cupintegration.in.model.IMapping;

import java.util.Map;

/**
 * Created by Sedelnikov FM on 19/01/2016.
 */
public class JiraFieldsParseUtils {

    private static final Logger LOG = Logger.getLogger(JiraFieldsParseUtils.class);

    public static void splitParamMap(Map<String, String> params, Map<String, String> defaultFields, Map<String, String> customFields, Map<String, IMapping> mappingMap) {
        if (MapUtils.isEmpty(params) || defaultFields == null || customFields == null || MapUtils.isEmpty(mappingMap)) {
            throw new NullArgumentException(params == null ? "params" : defaultFields == null ? "defaultFields" : customFields == null ? "customFields" : "mappingMap");
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            LOG.debug("Finding mapping for " + entry.getKey() + " tag");
            IMapping key;
            if ((key = mappingMap.get(entry.getKey())) != null) {
                String fieldId = key.getFieldId();
                String fieldType = key.getFieldType();
                String value = entry.getValue();
                LOG.debug("Founded for " + entry.getKey() + " tag " + fieldId + "[" + key.getFieldName() + "]");
                if (!fieldType.equals("Custom Field")) {
                    Object put = defaultFields.put(fieldId, value);
                    if (put != null) {
                        LOG.warn("Replacing defaultField value " + fieldId + ". Old value: " + put);
                    }
                } else {
                    Object put = customFields.put(fieldId, value);
                    if (put != null) {
                        LOG.warn("Replacing customFields value " + fieldId + ". Old value: " + put);
                    }
                }
            } else {
                LOG.debug("Nothing found for " + entry.getKey() + " tag");
            }
        }
    }
}
