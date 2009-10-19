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

import com.google.gdata.client.projecthosting.IssuesQuery;
import com.google.gdata.client.projecthosting.ProjectHostingService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.HtmlTextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.projecthosting.Cc;
import com.google.gdata.data.projecthosting.CcUpdate;
import com.google.gdata.data.projecthosting.IssueCommentsEntry;
import com.google.gdata.data.projecthosting.IssueCommentsFeed;
import com.google.gdata.data.projecthosting.IssuesEntry;
import com.google.gdata.data.projecthosting.IssuesFeed;
import com.google.gdata.data.projecthosting.Label;
import com.google.gdata.data.projecthosting.Updates;
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
        try {
            List<GCodeIssue> codeIssues = new ArrayList<GCodeIssue>();
            IssuesQuery issuesQuery = new IssuesQuery(issuesFeedUrl);
            issuesQuery.setAuthor(query.getReporter());
            issuesQuery.setCan(query.getCannedQuery());
            issuesQuery.setLabel(query.getLabel());
            issuesQuery.setOwner(query.getOwner());
            issuesQuery.setStatus(query.getStatus());
            issuesQuery.setFullTextQuery(query.getTextQuery());
            issuesQuery.setPublishedMax(getDatetime(query.DATE_FORMAT, query.getPublishedMax()));
            issuesQuery.setPublishedMin(getDatetime(query.DATE_FORMAT, query.getPublishedMin()));
            issuesQuery.setUpdatedMax(getDatetime(query.DATE_FORMAT, query.getUpdatedMax()));
            issuesQuery.setUpdatedMin(getDatetime(query.DATE_FORMAT, query.getUpdatedMin()));
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
        if (issuesEntry.hasLabels()) {
            List<Label> labels = issuesEntry.getLabels();
            for (Label label : labels) {
                codeIssue.addLable(label.getValue());
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
                    codeComment.addLable(label.getValue());
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

    private IssuesFeed queryIssues(IssuesQuery query)
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
