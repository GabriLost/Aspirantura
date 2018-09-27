package ru.sbertech.atlas.jira.cupintegration.in.model.enums;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Created by Sedelnikov FM on 29/01/2016.
 */
public enum ResultType {

    ISSUE("Issue"), RELEASE("Release"), UNDEFINED("Undefined");

    private final String type;

    private ResultType(String s) {
        type = s;
    }

    public boolean equalsType(String otherName) {
        return otherName != null && type.equals(otherName);
    }

    @JsonValue
    public String toString() {
        return this.type;
    }

    @JsonCreator
    public static ResultType forValue(String value) {
        if (value != null) {
            for (ResultType rt : ResultType.values()) {
                if (value.equalsIgnoreCase(rt.type)) {
                    return rt;
                }
            }
        }
        return null;
    }
}
