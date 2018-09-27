package ru.sbertech.atlas.jira.cupintegration.in.model;

/**
 * @author Dmitriev Vladimir
 */
public class ReleaseMapping {
    /**
     * ID of field, which contains release id from PPM
     */
    public String ppmReleaseId;
    /**
     * ID of field, which contains release name from PPM
     */
    public String ppmReleaseName;
    /**
     * ID of field, which contains project key of release from PPM
     */
    public String ppmReleaseAreaPs;
    /**
     * ID of field, which contains release start date from PPM
     */
    public String ppmReleaseStartDate;
    /**
     * ID of field, which contains release finish date from PPM
     */
    public String ppmReleaseFinishDate;
    /**
     * ID of field, which contains release status from PPM
     */
    public String ppmReleaseStatus;

    public ReleaseMapping() {
    }

    public ReleaseMapping(String ppmReleaseId, String ppmReleaseName, String ppmReleaseAreaPs, String ppmReleaseStartDate, String ppmReleaseFinishDate, String ppmReleaseStatus) {
        this.ppmReleaseId = ppmReleaseId;
        this.ppmReleaseName = ppmReleaseName;
        this.ppmReleaseAreaPs = ppmReleaseAreaPs;
        this.ppmReleaseStartDate = ppmReleaseStartDate;
        this.ppmReleaseFinishDate = ppmReleaseFinishDate;
        this.ppmReleaseStatus = ppmReleaseStatus;
    }

    public String getPpmReleaseId() {
        return ppmReleaseId;
    }

    public String getPpmReleaseName() {
        return ppmReleaseName;
    }

    public String getPpmReleaseAreaPs() {
        return ppmReleaseAreaPs;
    }

    public String getPpmReleaseStartDate() {
        return ppmReleaseStartDate;
    }

    public String getPpmReleaseFinishDate() {
        return ppmReleaseFinishDate;
    }

    public String getPpmReleaseStatus() {
        return ppmReleaseStatus;
    }
}
