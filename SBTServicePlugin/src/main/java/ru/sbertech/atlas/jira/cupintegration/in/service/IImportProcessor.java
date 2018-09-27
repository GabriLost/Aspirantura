package ru.sbertech.atlas.jira.cupintegration.in.service;

import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;

import java.util.Map;

/**
 * Created by Sedelnikov FM on 26/01/2016.
 */


/**
 * Import object from parameters map to Jira Storage
 */
public interface IImportProcessor {
    /**
     * Import one object
     * @param params Map with parameters of object
     * @return ImportResult - object with result of import procedure
     */
    ImportResult importObject(Map<String, String> params);
}
