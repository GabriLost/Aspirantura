package ru.sbertech.atlas.jira.cupintegration.out.xmlview;

import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.index.DefaultIndexManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.views.SingleIssueWriter;
import com.atlassian.jira.issue.views.util.SearchRequestViewBodyWriterUtil;
import com.atlassian.jira.issue.views.util.SearchRequestViewUtils;
import com.atlassian.jira.plugin.issueview.AbstractIssueView;
import com.atlassian.jira.plugin.searchrequestview.AbstractSearchRequestView;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestParams;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestViewModuleDescriptor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.JiraAuthenticationContextImpl;
import com.atlassian.jira.util.JiraVelocityUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.WriterOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbertech.atlas.jira.cupintegration.in.service.ImportSettingService;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public final class ExtendedSearchRequestView extends AbstractSearchRequestView {
    private static final Logger log = LoggerFactory.getLogger(ExtendedSearchRequestView.class);
    private static final String xsltFilePath = "ru/sbertech/atlas/jira/cupintegration/transform.xslt";

    private final JiraAuthenticationContext authenticationContext;
    private final SearchRequestViewBodyWriterUtil searchRequestViewBodyWriterUtil;
    private final ImportSettingService importSettingService;
    private SearchRequestViewModuleDescriptor moduleDescriptor;

    public ExtendedSearchRequestView(final JiraAuthenticationContext authenticationContext, final ExtendedSearchRequestViewBodyWriterUtil searchRequestViewBodyWriterUtil,
        ImportSettingService importSettingService) {
        this.authenticationContext = authenticationContext;
        this.searchRequestViewBodyWriterUtil = searchRequestViewBodyWriterUtil;
        this.importSettingService = importSettingService;
    }

    @Override
    public void init(SearchRequestViewModuleDescriptor moduleDescriptor) {
        this.moduleDescriptor = moduleDescriptor;
    }

    @Override
    public void writeSearchResults(final SearchRequest searchRequest, final SearchRequestParams searchRequestParams, final Writer writer) {
        final ExtendedIssueView xmlView = SearchRequestViewUtils.getIssueView(ExtendedIssueView.class);
        try {
            if (xmlView == null) {
                throw new RuntimeException("Could not find plugin of class '" + ExtendedIssueView.class.getName() + "'.  This is needed for this plugin to work");
            }

            resetCache();

            File tempFile = File.createTempFile("Non-transformed", "");
            try (final FileWriter fw = new FileWriter(tempFile)) {
                log.debug("XML start writing: " + tempFile.getAbsolutePath());
                fw.write(getHeader(searchRequest));
                final SingleIssueWriter singleIssueWriter = new SingleIssueWriter() {
                    public void writeIssue(final Issue issue, final AbstractIssueView issueView, final Writer writer) throws IOException {
                        log.debug("About to write XML view for issue [" + (issue != null ? issue.getKey() : "") + "].");
                        fw.write(issueView.getBody(issue, searchRequestParams));
                    }
                };
                searchRequestViewBodyWriterUtil.writeBody(fw, xmlView, searchRequest, singleIssueWriter, searchRequestParams.getPagerFilter());
                fw.write(getFooter());
            }
            log.debug("XML end writing: " + tempFile.getAbsolutePath());


            Source xslt = new StreamSource(ClassLoaderUtils.getResourceAsStream(xsltFilePath, this.getClass()));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xslt);

            File transformedFile = File.createTempFile("Transformed", "");

            transformer.setParameter("ID_KRP_CUSTOM_FIELD", "customfield_" + importSettingService.getImportSetting().cupKrpIdField);

            try (FileInputStream in = new FileInputStream(tempFile); FileOutputStream outputStream = new FileOutputStream(transformedFile)) {
                Source text = new StreamSource(new InputStreamReader(in, Charset.forName("utf-8")));
                transformer.transform(text, new StreamResult(outputStream));
            }

            try (WriterOutputStream output = new WriterOutputStream(writer, "utf-8"); FileInputStream transformedInputStream = new FileInputStream(transformedFile)) {
                IOUtils.copy(transformedInputStream, output);
            }
        } catch (final SearchException | IOException | TransformerException e) {
            log.error("Error while creating xml", e);
            throw new DataAccessException(e);
        }
    }

    /**
     * Reset JQL function result cache
     */
    private void resetCache() {
        JiraAuthenticationContextImpl.clearRequestCache();
        DefaultIndexManager.flushThreadLocalSearchers();
    }

    private String getHeader(final SearchRequest searchRequest) {
        final long startIssue = 0;
        final long endIssue = getSearchCount(searchRequest);

        final Map<String, Object> headerParams = JiraVelocityUtils.getDefaultVelocityParams(authenticationContext);
        headerParams.put("currentDate", new Date());
        headerParams.put("startissue", startIssue);
        headerParams.put("endissue", endIssue);
        headerParams.put("totalissue", endIssue);
        headerParams.put("buildDate", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

        //headerParams.put("customViewRequested", true);

        return moduleDescriptor.getHtml("header", headerParams);
    }

    private String getFooter() {
        return moduleDescriptor.getHtml("footer", Collections.<String, String>emptyMap());
    }

    /*
     * Get the total search count. The search count would first be retrieved from the SearchRequestParams. If not found,
     * retrieve using the search provider instead.
     */
    private long getSearchCount(final SearchRequest searchRequest) {
        try {
            return searchRequestViewBodyWriterUtil.searchCount(searchRequest);
        } catch (final SearchException se) {
            return 0;
        }
    }
}
