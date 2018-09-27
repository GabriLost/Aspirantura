package ru.sbertech.atlas.jira.cupintegration.out.xmlview;

import com.atlassian.jira.plugin.issueview.IssueViewFieldParams;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestParams;
import com.atlassian.jira.web.bean.PagerFilter;

import java.util.Map;

/**
 * Created by Yaroslav Astafiev on 25/11/2015.
 * Department of analytical solutions and system services improvement.
 *
 * Standart implementation need session
 */
public class ExtendedSearchRequestParams implements SearchRequestParams {

    private final IssueViewFieldParams issueViewFieldParams;
    private final PagerFilter pagerFilter;

    public ExtendedSearchRequestParams(final PagerFilter pagerFilter, final IssueViewFieldParams issueViewFieldParams) {
        this.issueViewFieldParams = issueViewFieldParams;
        this.pagerFilter = pagerFilter;
    }

    @Override
    public Map getSession() {
        return null;
    }

    @Override
    public PagerFilter getPagerFilter() {
        return pagerFilter;
    }

    @Override
    public String getUserAgent() {
        return null;
    }

    @Override
    public boolean isReturnMax() {
        return false;
    }

    @Override
    public void setReturnMax(boolean b) {

    }

    @Override
    public IssueViewFieldParams getIssueViewFieldParams() {
        return issueViewFieldParams;
    }

}
