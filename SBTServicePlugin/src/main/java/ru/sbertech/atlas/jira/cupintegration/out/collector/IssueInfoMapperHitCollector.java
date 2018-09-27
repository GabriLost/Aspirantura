package ru.sbertech.atlas.jira.cupintegration.out.collector;

import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.statistics.util.FieldableDocumentHitCollector;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;

import java.io.IOException;
import java.io.Writer;

public abstract class IssueInfoMapperHitCollector extends FieldableDocumentHitCollector{
    protected final Writer writer;
    private final IssueFactory issueFactory;

    public IssueInfoMapperHitCollector(Writer writer, IssueFactory issueFactory)
    {
        this.writer = writer;
        this.issueFactory = issueFactory;
    }

    @Override
    public void collect(Document d)
    {
        Issue issue = issueFactory.getIssue(d);
        try
        {
            writeIssue(issue, writer);
        }
        catch (IOException e)
        {
            throw new DataAccessException(e);
        }
    }

    @Override
    protected FieldSelector getFieldSelector() {
        return null;
    }

    protected abstract void writeIssue(Issue issue, Writer writer) throws IOException;
}
