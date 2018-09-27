package ru.sbertech.atlas.jira.cupintegration.in.service;

import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportSettingsUpdateException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;

/**
 * Created by SBT-Dmitriyev-VV on 10.02.2016.
 */
public interface ReleaseMappingService {
    /**
     * Return release settings for import releases from PPM to Jira
     *
     * @return {@link ReleaseMapping}
     */
    ReleaseMapping getReleaseMapping();

    /**
     * Create or update release settings for import releases from PPM to Jira
     *
     * @param releaseMapping
     * @throws IllegalArgumentException where ReleaseMapping is null
     * @throws ValidateException where one or more fields are inappropriate
     */
    void createOrUpdateReleaseMapping(ReleaseMapping releaseMapping) throws ImportSettingsUpdateException;
}
