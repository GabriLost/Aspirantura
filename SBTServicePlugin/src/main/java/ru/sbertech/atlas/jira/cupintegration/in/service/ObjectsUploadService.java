package ru.sbertech.atlas.jira.cupintegration.in.service;

import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;
import ru.sbertech.atlas.jira.cupintegration.in.validator.ImportSettingsChecker;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.atlassian.gzipfilter.org.apache.commons.lang.StringUtils.isEmpty;
import static java.util.Arrays.asList;

/**
 * Created by Sedelnikov FM on 26/01/2016.
 */
public class ObjectsUploadService {

    public static final String PPM_OBJECT_TYPE = "ppm_object_type";

    private static final List<String> OBJECT_TYPES = asList(ReleaseImportService.RELEASE, IssueImportService.TASK, IssueImportService.ZNI);
    private static final ConcurrentHashMap<String, IImportProcessor> importProcessors = new ConcurrentHashMap<>();

    private final SimpleDateFormat sdfDate = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.SSS");
    private final IssueImportService issueImportService;
    private final ReleaseImportService releaseImportService;
    private final ImportSettingService importSettingService;
    private final ReleaseMappingService releaseMappingService;

    public ObjectsUploadService(IssueImportService issueImportService, ReleaseImportService releaseImportService, ImportSettingService importSettingService,
        ReleaseMappingService releaseMappingService) {
        this.issueImportService = issueImportService;
        this.releaseImportService = releaseImportService;
        this.importSettingService = importSettingService;
        this.releaseMappingService = releaseMappingService;
        importProcessors.put(IssueImportService.TASK, this.issueImportService);
        importProcessors.put(IssueImportService.ZNI, this.issueImportService);
        importProcessors.put(ReleaseImportService.RELEASE, this.releaseImportService);
    }

    public List<ImportResult> upload(InputStream is) throws Exception {

        ImportSettings importSettings = importSettingService.getImportSetting();
        ReleaseMapping releaseMapping = releaseMappingService.getReleaseMapping();

        new ImportSettingsChecker().checkCustomField(importSettings.cupZniIdField).checkCustomField(importSettings.cupKrpIdField).checkUserName(importSettings.userName)
            .checkReleaseMapping(releaseMapping);

        String currentTime = sdfDate.format(new Date());

        List<ImportResult> result = new ArrayList<>();

        ImportResult importResult = new ImportResult(ResultType.UNDEFINED, ResultState.ERROR, "Unexpected behaviour");
        int nodeIndex = 0;
        XmlFileStaxReader staxReader = new XmlFileStaxReader(is, "object", "task");
        while (staxReader.hasNext()) {
            try {
                Map<String, String> params = staxReader.next();
                String objectType = params.get(PPM_OBJECT_TYPE);

                if (isEmpty(objectType)) {
                    importResult = new ImportResult(ResultType.UNDEFINED, ResultState.ERROR, "Undefined object! Object type tag is absent");
                    continue;
                }

                if (!OBJECT_TYPES.contains(objectType)) {
                    importResult = new ImportResult(ResultType.UNDEFINED, ResultState.ERROR, "Undefined object! Unknown object type tag " + objectType);
                    continue;
                }

                importResult = importProcessors.get(objectType).importObject(params);

            } catch (Exception e) {
                importResult = new ImportResult(e);
            } finally {
                importResult.setNodeIndex(++nodeIndex);
                importResult.setSyncId(currentTime);
                result.add(importResult);
            }
        }
        return result;
    }
}
