/*
 *  Copyright 2008 Anuradha.
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
package org.netbeans.cubeon.bugzilla.api.post.method;

import org.netbeans.cubeon.bugzilla.api.exception.BugzillaParsingException;
import org.netbeans.cubeon.bugzilla.api.model.BugDetails;
import org.netbeans.cubeon.bugzilla.api.post.handler.SaxBugDetailsHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.SAXParserFactory;

/**
 * This method will be used to get details of selected bug.
 *
 * @author radoslaw.holewa
 */
public class GetBugDetailsMethod extends BaseBugzillaPostMethod<BugDetails> {

    /**
     * Name of CGI script used to retrieve bug details.
     */
    private static final String SHOW_BUG_CGI_SCRIPT = "show_bug.cgi";

    /**
     * Two-arguments constructor.
     *
     * @param url   - url to Bugzilla repository
     * @param bugId - bug id, it's the id of the bug we want to load details
     */
    public GetBugDetailsMethod(String url, Integer bugId) {
        super(url);
        this.setParameter("ctype", "xml");
        this.setParameter("id", String.valueOf(bugId));
        this.setParameter("excludefield", "attachmentdata");
    }

    /**
     * Returns object representation of returned XML content.
     *
     * @return - {@see org.netbeans.cubeon.bugzilla.api.model.BugDetails} object representation of XML content
     * @throws BugzillaParsingException - throws exception if something goes wrong
     */
    public BugDetails getResult() throws BugzillaParsingException {
        try {
            return parseResponse(getResponseBodyAsStream());
        } catch (Exception e) {
            throw new BugzillaParsingException("Error during parsing received content.", e);
        }
    }

    /**
     * Parses XML content retrieved from InputStream.
     *
     * @param inputStream - input stream from which XML content will be retrieved
     * @return - {@see org.netbeans.cubeon.bugzilla.api.model.BugDetails} instance which will
     *         represent XML document data
     * @throws SAXException                 - throws exception if something goes wrong (it's SAX parsing exception)
     * @throws IOException                  - throws exception if something goes wrong
     * @throws ParserConfigurationException - throws this exception in case there are any problems during initialization
     *                                      of parser's configuration
     */
    private BugDetails parseResponse(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        SaxBugDetailsHandler handler = new SaxBugDetailsHandler();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(inputStream, handler);
        return handler.getBugDetails();
    }

    /**
     * {@inheritDoc}
     */
    public String getScriptName() {
        return SHOW_BUG_CGI_SCRIPT;
    }
}
