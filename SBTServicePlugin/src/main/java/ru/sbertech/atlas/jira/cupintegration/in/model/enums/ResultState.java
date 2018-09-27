package ru.sbertech.atlas.jira.cupintegration.in.model.enums;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Created by Sedelnikov FM on 29/01/2016.
 */
public enum ResultState {

    CREATED("создан"), UPDATED("обновлен"), ERROR("ошибка");

    private final String state;

    private ResultState(String s) {
        state = s;
    }

    public boolean equalsState(String otherState) {
        return otherState != null && state.equals(otherState);
    }

    @JsonValue
    public String toString() {
        return this.state;
    }

    @JsonCreator
    public static ResultState forValue(String value) {
        if (value != null) {
            for (ResultState rs : ResultState.values()) {
                if (value.equalsIgnoreCase(rs.state)) {
                    return rs;
                }
            }
        }
        return null;
    }
}
