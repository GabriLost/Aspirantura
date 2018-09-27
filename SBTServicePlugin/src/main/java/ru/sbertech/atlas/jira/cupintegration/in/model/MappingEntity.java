package ru.sbertech.atlas.jira.cupintegration.in.model;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Unique;

/**
 * @author Dmitriy Klabukov
 */
@Preload
public interface MappingEntity extends Entity, IMapping {

    @Override
    @NotNull
    @Unique
    @Accessor("XML_ID")
    String getXmlId();

    @Override
    void setXmlId(String xmlId);

    @Override
    @NotNull
    @Accessor("FIELD_TYPE")
    String getFieldType();

    @Override
    void setFieldType(String fieldType);

    @Override
    @NotNull
    @Accessor("FIELD_NAME")
    String getFieldName();

    @Override
    void setFieldName(String fieldName);

    @Override
    @NotNull
    @Accessor("FIELD_ID")
    String getFieldId();

    @Override
    void setFieldId(String fieldId);
}
