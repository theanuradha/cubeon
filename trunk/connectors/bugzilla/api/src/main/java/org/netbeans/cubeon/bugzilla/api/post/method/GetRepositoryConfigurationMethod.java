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
import org.netbeans.cubeon.bugzilla.api.model.RepositoryConfiguration;
import org.netbeans.cubeon.bugzilla.api.post.handler.SaxRepositoryConfigurationHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.SAXParserFactory;

/**
 * This metod will be used to retrieve information about Bugzilla remote repository specific values
 * like available products, versions etc.
 *
 * @author radoslaw.holewa
 */
public class GetRepositoryConfigurationMethod extends BaseBugzillaPostMethod<RepositoryConfiguration> {

    /**
     * Name of CGI script that will be used to receive repository configuration.
     */
    private static final String SCRIPT_NAME = "config.cgi";

    /**
     * Constructor of Bugzilla POST method which will be used to retrieve repository
     * related configuration.
     *
     * @param url - Bugzilla repository URL address
     */
    public GetRepositoryConfigurationMethod(String url) {
        super(url);
        setParameter("ctype", "rdf");
    }

    /**
     * {@inheritDoc}
     */
    public RepositoryConfiguration getResult() throws BugzillaParsingException {
        try {
            return parseResponse(getResponseBodyAsStream());
        } catch (Exception e) {
            throw new BugzillaParsingException("Error during parsing received content.", e);
        }
    }

    /**
     * Parses response.
     *
     * @param inputStream - input stream which contains content of repository configuration response
     * @return - object which contains repository configuration
     * @throws IOException - throws exception in case of problems during parsing
     * @throws ParserConfigurationException - throws exception in case of any problems during initialization
     * of parsers configuration
     */
    private RepositoryConfiguration parseResponse(InputStream inputStream) throws SAXException, ParserConfigurationException, IOException {
        SaxRepositoryConfigurationHandler handler = new SaxRepositoryConfigurationHandler();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(inputStream, handler);
        return handler.getRepositoryConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    public String getScriptName() {
        return SCRIPT_NAME;
    }
}
