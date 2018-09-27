package ru.sbertech.atlas.jira.cupintegration.in.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitriev Vladimir
 */
public class ReleaseMappingTest {
    @Test
    public void testReleaseMapping_WithArguments() throws Exception {
        String ppmReleaseId = "ppm_release_id";
        String ppmReleaseName = "ppm_release_name";
        String ppmReleaseAreaPs = "ppm_release_area_ps";
        String ppmReleaseStartDate = "ppm_release_start_date";
        String ppmReleaseFinishDate = "ppm_release_finish_date";
        String ppmReleaseStatus = "ppm_release_status";

        ReleaseMapping releaseMapping = new ReleaseMapping(ppmReleaseId, ppmReleaseName, ppmReleaseAreaPs, ppmReleaseStartDate, ppmReleaseFinishDate, ppmReleaseStatus);

        assertEquals(ppmReleaseId, releaseMapping.getPpmReleaseId());
        assertEquals(ppmReleaseName, releaseMapping.getPpmReleaseName());
        assertEquals(ppmReleaseAreaPs, releaseMapping.getPpmReleaseAreaPs());
        assertEquals(ppmReleaseStartDate, releaseMapping.getPpmReleaseStartDate());
        assertEquals(ppmReleaseFinishDate, releaseMapping.getPpmReleaseFinishDate());
        assertEquals(ppmReleaseStatus, releaseMapping.getPpmReleaseStatus());
    }
}
