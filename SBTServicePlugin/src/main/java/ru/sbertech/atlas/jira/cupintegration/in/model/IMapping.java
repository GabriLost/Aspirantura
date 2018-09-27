package ru.sbertech.atlas.jira.cupintegration.in.model;

/**
 * Base interface for field mapping objects
 *
 * @author Dmitriy Klabukov
 */
public interface IMapping {

    String getXmlId();

    void setXmlId(String xmlId);

    String getFieldType();

    void setFieldType(String fieldType);

    String getFieldName();

    void setFieldName(String fieldName);

    String getFieldId();

    void setFieldId(String fieldId);

}
