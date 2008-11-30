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

import au.com.bytecode.opencsv.CSVReader;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaParsingException;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaException;
import org.netbeans.cubeon.bugzilla.api.model.BugSummary;
import org.netbeans.cubeon.bugzilla.api.post.queries.BaseQuery;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * POST method
 *
 * @author radoslaw.holewa
 */
public class QueryBugsListPostMethod extends BaseBugzillaPostMethod<List<BugSummary>> {

    /**
     * Name of CGI script that will be used to receive result of this query.
     */
    public static final String SCRIPT_NAME = "buglist.cgi";

    /**
     * Constructor of query bugs list Bugzilla POST method.
     *
     * @param url   - Bugzilla repository URL address
     * @param query - query used to get bugs list
     * @throws BugzillaException - throws exception in case there are any problems during method initialization
     */
    public QueryBugsListPostMethod(String url, BaseQuery query) throws BugzillaException {
        super(url);
        setParameter("ctype", "csv");
        Map<String, String> paramsMap = query.parametersMap();
        for (String paramName : paramsMap.keySet()) {
            setParameter(paramName, paramsMap.get(paramName));
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<BugSummary> getResult() throws BugzillaParsingException {
        try {
            return parseResponse(getResponseBodyAsStream());
        } catch (Exception e) {
            throw new BugzillaParsingException("Error during parsing received content.", e);
        }
    }

    /**
     * Parses response.
     *
     * @param inputStream - input stream which contains content of query response
     * @return - list of bugs
     * @throws BugzillaParsingException - throws exception in case of problems during parsing
     */
    private List<BugSummary> parseResponse(InputStream inputStream) throws BugzillaParsingException {
        CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
        List<BugSummary> result = new ArrayList<BugSummary>();
        /**
         * At the begining we need to read the first line of file, this line
         * contains names of CSV file fields
         */
        try {
            String[] nextLine = reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                result.add(parseLine(nextLine));
            }
            return result;
        } catch( IOException e ) {
            throw new BugzillaParsingException( "Error while parsing query response lines.", e); 
        }
    }

    /**
     * Parses one line of CSV file.
     *
     * @param lineContent - line content to parse
     * @return - object representation of given line
     * @throws BugzillaParsingException - throws exception in case of problems during parsing
     */
    private BugSummary parseLine(String[] lineContent) throws BugzillaParsingException {
        BugSummary bug = new BugSummary();
        if(lineContent.length < 8) {
          throw new BugzillaParsingException("Error during line parsing, invalid number of line elements.");
        }
        bug.setId(lineContent[0]);
        bug.setSeverity(lineContent[1]);
        bug.setPriority(lineContent[2]);
        bug.setOperatingSystem(lineContent[3]);
        bug.setAssignee(lineContent[4]);
        bug.setStatus(lineContent[5]);
        bug.setResolution(lineContent[6]);
        bug.setSummary(lineContent[7]);
        return bug;
    }

    /**
     * {@inheritDoc}
     */
    public String getScriptName() {
        return SCRIPT_NAME;
    }
}

