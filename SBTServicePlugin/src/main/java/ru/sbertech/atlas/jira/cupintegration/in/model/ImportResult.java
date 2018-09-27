package ru.sbertech.atlas.jira.cupintegration.in.model;

import org.codehaus.jackson.annotate.JsonProperty;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;

//import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Sedelnikov FM on 29/01/2016.
 */
public class ImportResult {

    public ImportResult(ResultType type, ResultState state, String value) {
        this.type = type;
        this.state = state;
        this.value = value;
    }

    public ImportResult(Exception e) {
        this.type = ResultType.UNDEFINED;
        this.state = ResultState.ERROR;
        this.value = e.getMessage();
    }

    public ImportResult(ResultType type, ResultState state, String value, Long objectId, String projectKey) {
        this.type = type;
        this.state = state;
        this.value = value;
        this.objectId = objectId;
        this.projectKey = projectKey;
    }

    /**
     * Type of import object
     */
    @JsonProperty
    private ResultType type;

    /**
     * Import result state
     */
    @JsonProperty
    private ResultState state;

    /**
     * Import result value:
     * Could contain string equivalent of created/updated object or error message
     */
    @JsonProperty
    private String value;

    /**
     * Unique Id of import object
     */
    @JsonProperty
    private Long objectId;

    /**
     * Project key
     */
    @JsonProperty
    private String projectKey;

    /**
     * Index node in uploaded file which contain current object
     */
    @JsonProperty
    private Integer nodeIndex;

    /**
     * Unique Id for uploaded file
     */
    @JsonProperty
    private String syncId;


    public ResultType getType() {
        return type;
    }

    public ResultState getState() {
        return state;
    }

    public String getValue() {
        return value;
    }

    public Long getObjectId() {
        return objectId;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public Integer getNodeIndex() {
        return nodeIndex;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public void setNodeIndex(Integer nodeIndex) {
        this.nodeIndex = nodeIndex;
    }


}
