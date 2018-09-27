package ru.sbertech.atlas.jira.cupintegration.out.xmlview;

import com.atlassian.jira.plugin.issueview.IssueViewFieldParams;
import com.atlassian.jira.web.bean.PagerFilter;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Yaroslav Astafiev on 17/03/2016.
 * Department of analytical solutions and system services improvement.
 */
public class ExtendedSearchRequestParamsTest {

    private static PagerFilter pagerFilter;
    private static IssueViewFieldParams issueViewFieldParams;

    @BeforeClass
    public static void init() {
        pagerFilter = mock(PagerFilter.class);
        issueViewFieldParams = mock(IssueViewFieldParams.class);
    }

    @Test
    public void testGetSession() throws Exception {
        ExtendedSearchRequestParams extendedSearchRequestParams = new ExtendedSearchRequestParams(pagerFilter, issueViewFieldParams);

        assertNull(extendedSearchRequestParams.getSession());
    }

    @Test
    public void testGetPagerFilter() throws Exception {
        ExtendedSearchRequestParams extendedSearchRequestParams = new ExtendedSearchRequestParams(pagerFilter, issueViewFieldParams);

        assertEquals(pagerFilter, extendedSearchRequestParams.getPagerFilter());
    }

    @Test
    public void testGetUserAgent() throws Exception {
        ExtendedSearchRequestParams extendedSearchRequestParams = new ExtendedSearchRequestParams(pagerFilter, issueViewFieldParams);

        assertNull(extendedSearchRequestParams.getUserAgent());
    }

    @Test
    public void testGetIssueViewFieldParams() throws Exception {
        ExtendedSearchRequestParams extendedSearchRequestParams = new ExtendedSearchRequestParams(pagerFilter, issueViewFieldParams);

        assertEquals(issueViewFieldParams, extendedSearchRequestParams.getIssueViewFieldParams());
    }
}
