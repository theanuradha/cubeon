/*
 *  Copyright 2009 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.netbeans.cubeon.gcode.internals;

import com.google.gdata.client.Query;
import com.google.gdata.client.projecthosting.IssuesQuery;
import com.google.gdata.client.projecthosting.ProjectHostingService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.HtmlTextConstruct;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.projecthosting.Cc;
import com.google.gdata.data.projecthosting.CcUpdate;
import com.google.gdata.data.projecthosting.IssueCommentsEntry;
import com.google.gdata.data.projecthosting.IssueCommentsFeed;
import com.google.gdata.data.projecthosting.IssuesEntry;
import com.google.gdata.data.projecthosting.IssuesFeed;
import com.google.gdata.data.projecthosting.Label;
import com.google.gdata.data.projecthosting.Owner;
import com.google.gdata.data.projecthosting.OwnerUpdate;
import com.google.gdata.data.projecthosting.SendEmail;
import com.google.gdata.data.projecthosting.Status;
import com.google.gdata.data.projecthosting.Summary;
import com.google.gdata.data.projecthosting.Updates;
import com.google.gdata.data.projecthosting.Username;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.cubeon.gcode.api.GCodeComment;
import org.netbeans.cubeon.gcode.api.GCodeException;
import org.netbeans.cubeon.gcode.api.GCodeIssue;
import org.netbeans.cubeon.gcode.api.GCodeIssueUpdate;
import org.netbeans.cubeon.gcode.api.GCodeQuery;
import org.netbeans.cubeon.gcode.api.GCodeSession;
import org.netbeans.cubeon.gcode.api.GCodeState;

/**
 *
 * @author Anuradha
 */
public class GCodeSessionImpl implements GCodeSession {

    private static final String FEED_URI_BASE =
            "http://code.google.com/feeds/issues";
    //Value of zero indicates that the server is free to determine the maximum value.
    private static final int RESULT_LIMIT = 0;
    private final ProjectHostingService service;
    private static final String PROJECTION = "/full";
    /** Issues API base URL constructed from the given project name. */
    private String issuesBaseUri;
    /** Default issues feed URL constructed from the given project name. */
    private URL issuesFeedUrl;
    /** Group 1 of the regex will match the ID of the issue. */
    private Pattern issueIdPattern;
    /** Group 1 of the regex will match the ID of the comment. */
    private Pattern commentIdPattern;

    public GCodeSessionImpl(String project, String user, String password) throws GCodeException {
        service = new ProjectHostingService("cubeon-client");
        try {
            if ((user != null) && (password != null)) {

                service.setUserCredentials(user, password);

            }

            issuesBaseUri = FEED_URI_BASE + "/p/" + project + "/issues";
            issuesFeedUrl = makeIssuesFeedUrl(project);
            issueIdPattern = Pattern.compile(
                    issuesBaseUri + PROJECTION + "/(\\d+)$");
            commentIdPattern = Pattern.compile(
                    issuesBaseUri + "/\\d+/comments" + PROJECTION + "/(\\d+)$");
        } catch (AuthenticationException ex) {
            throw new GCodeException(ex);
        } catch (MalformedURLException ex) {
            throw new GCodeException(ex);
        }
    }

    public GCodeIssue createIssue(GCodeIssue codeIssue, boolean notify) throws GCodeException {
        try {
            IssuesEntry entry = new IssuesEntry();
            Person author = new Person();
            author.setName(codeIssue.getReportedBy());
            entry.getAuthors().add(author);
            if (codeIssue.getOwner() != null) {
                Owner owner = new Owner();
                owner.setUsername(new Username(codeIssue.getOwner()));
                entry.setOwner(owner);
            }
            for (String ccName : codeIssue.getCcs()) {
                Cc cc = new Cc();
                cc.setUsername(new Username(ccName));
                entry.addCc(cc);
            }
            for (String label : codeIssue.getLabels()) {
                entry.addLabel(new Label(label));
            }
            entry.setContent(new HtmlTextConstruct(codeIssue.getDescription()));
            entry.setTitle(new PlainTextConstruct(codeIssue.getSummary()));
            entry.setStatus(new Status(codeIssue.getStatus()));
            if (notify) {
                entry.setSendEmail(new SendEmail("True"));
            } else {
                entry.setSendEmail(new SendEmail("False"));
            }
            IssuesEntry issueInserted = insertIssue(entry);
            return toGCodeIssue(issueInserted);
        } catch (IOException ex) {
            throw new GCodeException(ex);
        } catch (ServiceException ex) {
            throw new GCodeException(ex);
        }
    }

    public GCodeIssue updateIssue(GCodeIssueUpdate issueUpdate, boolean notify) throws GCodeException {
        try {
            // Create issue updates
            Updates updates = new Updates();
            Person author = new Person();
            author.setName(issueUpdate.getAuthor());
            if (issueUpdate.getSummary() != null) {
                updates.setSummary(new Summary(issueUpdate.getSummary()));
            }
            if (issueUpdate.getStatus() != null) {
                updates.setStatus(new Status(issueUpdate.getStatus()));
            }
            if (issueUpdate.getOwner() != null) {
                updates.setOwnerUpdate(new OwnerUpdate(issueUpdate.getOwner()));
            }
            for (String label : issueUpdate.getLabels()) {
                updates.addLabel(new Label(label));
            }
            for (String cc : issueUpdate.getCcs()) {
                updates.addCcUpdate(new CcUpdate(cc));
            }
            IssueCommentsEntry entry = new IssueCommentsEntry();
            entry.getAuthors().add(author);
            entry.setContent(new HtmlTextConstruct(issueUpdate.getComment()));
            entry.setUpdates(updates);
            if (notify) {
                entry.setSendEmail(new SendEmail("True"));
            } else {
                entry.setSendEmail(new SendEmail("False"));
            }
            insertComment(issueUpdate.getIssueId(), entry);
            //read issue again
            return getIssue(issueUpdate.getIssueId());
        } catch (IOException ex) {
            throw new GCodeException(ex);
        } catch (ServiceException ex) {
            throw new GCodeException(ex);
        }
    }

    public GCodeIssue getIssue(String id) throws GCodeException {
        try {
            IssuesEntry issuesEntry = getIssueEntry(String.valueOf(id));
            GCodeIssue codeIssue = toGCodeIssue(issuesEntry);
            return codeIssue;
        } catch (IOException ex) {
            throw new GCodeException(ex);
        } catch (ServiceException ex) {
            throw new GCodeException(ex);
        }
    }

    public List<GCodeIssue> getIssues(String... ids) throws GCodeException {
        List<GCodeIssue> codeIssues = new ArrayList<GCodeIssue>();
        for (String id : ids) {
            codeIssues.add(getIssue(id));
        }
        return codeIssues;
    }

    public List<GCodeIssue> getIssuesByQuery(GCodeQuery query) throws GCodeException {
        return getIssuesByQuery(query, RESULT_LIMIT);
    }

    public List<GCodeIssue> getIssuesByQueryString(String query) throws GCodeException {
        return getIssuesByQueryString(query, RESULT_LIMIT);
    }

    public List<GCodeIssue> getIssuesByQuery(GCodeQuery query, int resultLimit) throws GCodeException {
        try {
            List<GCodeIssue> codeIssues = new ArrayList<GCodeIssue>();
            IssuesQuery issuesQuery = new IssuesQuery(issuesFeedUrl);
            issuesQuery.setAuthor(query.getReporter());
            issuesQuery.setCan(query.getCannedQuery());
            issuesQuery.setLabel(query.getLabel());
            issuesQuery.setOwner(query.getOwner());
            issuesQuery.setStatus(query.getStatus());
            issuesQuery.setPublishedMax(getDatetime(query.DATE_FORMAT, query.getPublishedMax()));
            issuesQuery.setPublishedMin(getDatetime(query.DATE_FORMAT, query.getPublishedMin()));
            issuesQuery.setUpdatedMax(getDatetime(query.DATE_FORMAT, query.getUpdatedMax()));
            issuesQuery.setUpdatedMin(getDatetime(query.DATE_FORMAT, query.getUpdatedMin()));
            issuesQuery.setMaxResults(resultLimit);
            IssuesFeed queryIssues = queryIssues(issuesQuery);
            for (IssuesEntry issuesEntry : queryIssues.getEntries()) {
                codeIssues.add(toGCodeIssue(issuesEntry));
            }
            return codeIssues;
        } catch (IOException ex) {
            throw new GCodeException(ex);
        } catch (ServiceException ex) {
            throw new GCodeException(ex);
        }
    }

    public List<GCodeIssue> getIssuesByQueryString(String query, int resultLimit) throws GCodeException {
        try {
            List<GCodeIssue> codeIssues = new ArrayList<GCodeIssue>();
            Query issuesQuery = new Query(issuesFeedUrl);
            issuesQuery.setFullTextQuery(query);
            issuesQuery.setMaxResults(resultLimit);
            IssuesFeed queryIssues = queryIssues(issuesQuery);
            for (IssuesEntry issuesEntry : queryIssues.getEntries()) {
                codeIssues.add(toGCodeIssue(issuesEntry));
            }
            return codeIssues;
        } catch (IOException ex) {
            throw new GCodeException(ex);
        } catch (ServiceException ex) {
            throw new GCodeException(ex);
        }
    }

    private DateTime getDatetime(String format, String dataString) {
        if (dataString != null) {
            try {
                Date parse = new SimpleDateFormat(format).parse(dataString);
                return new DateTime(parse);
            } catch (ParseException ex) {
                Logger.getLogger(GCodeSessionImpl.class.getName()).warning(ex.getMessage());
            }
        }
        return null;
    }

    private GCodeIssue toGCodeIssue(IssuesEntry issuesEntry) throws IOException, ServiceException {
        String description = "";
        TextContent textContent = (TextContent) issuesEntry.getContent();
        if ((textContent != null) && (textContent.getContent() != null)) {
            HtmlTextConstruct textConstruct = (HtmlTextConstruct) textContent.getContent();
            description = textConstruct.getHtml();
        }
        GCodeIssue codeIssue = new GCodeIssue(getIssueId(issuesEntry.getId()), issuesEntry.getTitle().getPlainText(), description);
        codeIssue.setReportedBy(issuesEntry.getAuthors().get(0).getName());
        if (issuesEntry.hasState()) {
            codeIssue.setState(GCodeState.valueOf(issuesEntry.getState().getValue().name()));
        }
        if (issuesEntry.hasStatus()) {
            codeIssue.setStatus(issuesEntry.getStatus().getValue());
        }
        if (issuesEntry.hasStars()) {
            codeIssue.setStars(issuesEntry.getStars().getValue());
        }
        if (issuesEntry.hasOwner()) {
            codeIssue.setOwner(issuesEntry.getOwner().getUsername().getValue());
        }
        if (issuesEntry.getPublished() != null) {
            codeIssue.setCreatedDate(issuesEntry.getPublished().getValue());
        }
        if (issuesEntry.getUpdated() != null) {
            codeIssue.setUpdatedDate(issuesEntry.getUpdated().getValue());
        }
        if (issuesEntry.hasLabels()) {
            List<Label> labels = issuesEntry.getLabels();
            for (Label label : labels) {
                codeIssue.addLabel(label.getValue());
            }
        }
        if (issuesEntry.hasCcs()) {
            List<Cc> ccs = issuesEntry.getCcs();
            for (Cc cc : ccs) {
                codeIssue.addCc(cc.getUsername().getValue());
            }
        }
        //read comments
        IssueCommentsFeed commentsFeed = getCommentsFeed(codeIssue.getId());
        for (IssueCommentsEntry commentEntry : commentsFeed.getEntries()) {
            GCodeComment codeComment = new GCodeComment(getCommentId(commentEntry.getId()),
                    commentEntry.getAuthors().get(0).getName());
            TextContent commentContent = (TextContent) commentEntry.getContent();
            if ((commentContent != null) && (commentContent.getContent() != null)) {
                HtmlTextConstruct textConstruct =
                        (HtmlTextConstruct) commentContent.getContent();
                codeComment.setComment(textConstruct.getHtml());
            }
            if (commentEntry.getPublished() != null) {
                codeComment.setCommentDate(commentEntry.getPublished().getValue());
            }
            if (commentEntry.hasUpdates()) {
                Updates updates = commentEntry.getUpdates();

                if (updates.hasSummary()) {
                    codeComment.setSummary(updates.getSummary().getValue());
                }

                if (updates.hasStatus()) {
                    codeComment.setStatus(updates.getStatus().getValue());
                }

                if (updates.hasOwnerUpdate()) {
                    codeComment.setOwner(updates.getOwnerUpdate().getValue());
                }

                for (Label label : updates.getLabels()) {
                    codeComment.addLabel(label.getValue());
                }

                for (CcUpdate cc : updates.getCcUpdates()) {
                    codeComment.addCc(cc.getValue());
                }

            }

            codeIssue.addComment(codeComment);
        }
        return codeIssue;
    }

    //util methods copied from google gdata sample code 
    private URL makeIssuesFeedUrl(String proj)
            throws MalformedURLException {
        return new URL(FEED_URI_BASE + "/p/" + proj + "/issues" + PROJECTION);
    }

    private URL makeIssueEntryUrl(String issueId)
            throws MalformedURLException {
        return new URL(issuesBaseUri + PROJECTION + "/" + issueId);
    }

    private URL makeCommentsFeedUrl(String issueId)
            throws MalformedURLException {
        return new URL(issuesBaseUri + "/" + issueId + "/comments" + PROJECTION);
    }

    private URL makeCommentEntryUrl(String issueId, String commentId)
            throws MalformedURLException {
        return new URL(issuesBaseUri + "/" + issueId + "/comments" + PROJECTION
                + "/" + commentId);
    }

    private IssuesFeed getIssuesFeed(URL feedUrl)
            throws IOException, ServiceException {
        return service.getFeed(feedUrl, IssuesFeed.class);
    }

    private IssuesEntry getIssueEntry(String issueId)
            throws IOException, ServiceException {
        return getIssueEntry(makeIssueEntryUrl(issueId));
    }

    private IssuesEntry getIssueEntry(URL entryUrl)
            throws IOException, ServiceException {
        return service.getEntry(entryUrl, IssuesEntry.class);
    }

    private IssueCommentsFeed getCommentsFeed(String issueId)
            throws IOException, ServiceException {
        return getCommentsFeed(makeCommentsFeedUrl(issueId));
    }

    private IssueCommentsFeed getCommentsFeed(URL feedUrl)
            throws IOException, ServiceException {
        return service.getFeed(feedUrl, IssueCommentsFeed.class);
    }

    private IssueCommentsEntry getCommentEntry(
            String issueId, String commentId)
            throws IOException, ServiceException {
        return getCommentEntry(makeCommentEntryUrl(issueId, commentId));
    }

    private IssueCommentsEntry getCommentEntry(URL entryUrl)
            throws IOException, ServiceException {
        return service.getEntry(entryUrl, IssueCommentsEntry.class);
    }

    private IssuesEntry insertIssue(IssuesEntry entry)
            throws IOException, ServiceException {
        return service.insert(issuesFeedUrl, entry);
    }

    private IssueCommentsEntry insertComment(
            String issueId, IssueCommentsEntry entry)
            throws IOException, ServiceException {
        return insertComment(makeCommentsFeedUrl(issueId), entry);
    }

    private IssueCommentsEntry insertComment(
            URL commentsFeedUrl, IssueCommentsEntry entry)
            throws IOException, ServiceException {
        return service.insert(commentsFeedUrl, entry);
    }

    private IssuesFeed queryIssues(Query query)
            throws IOException, ServiceException {
        return service.query(query, IssuesFeed.class);
    }

    private String getIssueId(String issueUrl) {
        Matcher matcher = issueIdPattern.matcher(issueUrl);
        return matcher.matches() ? matcher.group(1) : null;
    }

    private String getCommentId(String commentUrl) {
        Matcher matcher = commentIdPattern.matcher(commentUrl);
        return matcher.matches() ? matcher.group(1) : null;
    }
}
