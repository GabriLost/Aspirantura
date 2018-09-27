package ru.sbertech.atlas.jira.cupintegration.in.repository;

import ru.sbertech.atlas.jira.cupintegration.in.model.IMapping;

import java.util.Map;

/**
 * Interface for persist provider for object mappings
 *
 * @author Dmitriy Klabukov
 */
public interface IMappingRepository {

    /**
     * Returns all mapping from repository
     *
     * @return all object in repository
     */
    IMapping[] getAll();

    /**
     *  Returns all mapping from repository
     *  key - XmlId
     * @return map with all object in repository
     */
    Map<String, IMapping> getMappingsMap();

    /**
     * Bulk importObject of Mappings
     *
     * @param mapping object to importObject
     * @return created objects
     */
    IMapping[] createAll(IMapping[] mapping);

    /**
     * Clear repository
     *
     * @return deleted objects
     */
    IMapping[] deleteAll();

    IMapping getFieldIdForKey(String key);

}
