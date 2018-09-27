package ru.sbertech.atlas.jira.cupintegration.in.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Dmitriy Klabukov
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class JsonMapping implements IMapping {

    private String xmlId;
    private String fieldType;
    private String fieldName;
    private String fieldId;

    /**
     * Empty constructor for instantiate by jackson
     */
    @SuppressWarnings("unused")
    public JsonMapping() {
    }

    public JsonMapping(String xmlid, String fieldType, String fieldName, String fieldId) {
        this.xmlId = xmlid;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.fieldId = fieldId;
    }

    /**
     * Copy constructor
     *
     * @param mapping object to copy
     */
    public JsonMapping(IMapping mapping) {
        this(mapping.getXmlId(), mapping.getFieldType(), mapping.getFieldName(), mapping.getFieldId());
    }

    @Override
    public String getXmlId() {
        return xmlId;
    }

    @Override
    public void setXmlId(String xmlId) {
        this.xmlId = xmlId;
    }

    @Override
    public String getFieldType() {
        return fieldType;
    }

    @Override
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldId() {
        return fieldId;
    }

    @Override
    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }
}
