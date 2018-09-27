package ru.sbertech.atlas.jira.cupintegration.in.repository;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;
import net.java.ao.Query;
import ru.sbertech.atlas.jira.cupintegration.in.model.IMapping;
import ru.sbertech.atlas.jira.cupintegration.in.model.MappingEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Persisting mappings in database using active objects
 *
 * @author Dmitriy Klabukov
 */
public final class ActiveObjectMappingRepository implements IMappingRepository {

    private ActiveObjects activeObjects;

    public ActiveObjectMappingRepository(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    @Override
    public IMapping[] getAll() {
        return activeObjects.find(MappingEntity.class);
    }

    @Override
    public Map<String, IMapping> getMappingsMap() {
        Map<String, IMapping> mappingMap = new HashMap<>();
        for (IMapping mapping : getAll()) {
            mappingMap.put(mapping.getXmlId(), mapping);
        }
        return mappingMap;
    }

    @Override
    public IMapping[] createAll(IMapping[] mapping) {

        Set<IMapping> mappings = new HashSet<>();

        for (IMapping m : mapping) {
            mappings.add(activeObjects
                .create(MappingEntity.class, new DBParam("XML_ID", m.getXmlId()), new DBParam("FIELD_TYPE", m.getFieldType()), new DBParam("FIELD_NAME", m.getFieldName()),
                    new DBParam("FIELD_ID", m.getFieldId())));
        }

        return mappings.toArray(new IMapping[mappings.size() - 1]);
    }

    @Override
    public IMapping[] deleteAll() {
        MappingEntity[] entities = activeObjects.find(MappingEntity.class);
        activeObjects.delete(entities);
        return entities;
    }

    @Override
    public IMapping getFieldIdForKey(String key) {
        MappingEntity[] entities = activeObjects.find(MappingEntity.class, Query.select().where("XML_ID = ?", key));
        return entities.length > 0 ? entities[0] : null;
    }
}
