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
import org.netbeans.cubeon.bugzilla.api.model.BugSummary;
import org.netbeans.cubeon.bugzilla.api.post.queries.BaseQuery;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * //@todo class description
 *
 * @author radoslaw.holewa
 */
public class QueryBugsListPostMethod extends BaseBugzillaPostMethod<List<BugSummary>> {

    private static final String SCRIPT_NAME = "buglist.cgi";

    /**
     * Constructor of query bugs list Bugzilla POST method.
     *
     * @param url   - Bugzilla repository URL address
     * @param query - query used to get bugs list
     */
    public QueryBugsListPostMethod(String url, BaseQuery query) {
        super(url);
        setParameter("ctype", "csv");
        Map<String, String> paramsMap = query.parametersMap();
        for (String paramName : paramsMap.keySet()) {
            setParameter(paramName, paramsMap.get(paramName));
        }
    }

    public List<BugSummary> getResult() throws BugzillaParsingException {
        try {
            return parseResponse(getResponseBodyAsStream());
        } catch (Exception e) {
            throw new BugzillaParsingException("Error during parsing received content.", e);
        }
    }

    private List<BugSummary> parseResponse(InputStream inputStream) throws SAXException, ParserConfigurationException, IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
        List<BugSummary> result = new ArrayList<BugSummary>();
        /**
         * at the begining we need to read the first line of file, this line
         * contains names of CSV file fields
         */
        String[] nextLine = reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
            result.add(parseLine(nextLine));
        }
        return result;
    }

    private BugSummary parseLine(String[] nextLine) {
        BugSummary bug = new BugSummary();
        bug.setId(Integer.parseInt(nextLine[0]));
        bug.setSeverity(nextLine[1]);
        bug.setPriority(nextLine[2]);
        bug.setOperatingSystem(nextLine[3]);
        bug.setAssignee(nextLine[4]);
        bug.setStatus(nextLine[5]);
        bug.setResolution(nextLine[6]);
        bug.setSummary(nextLine[7]);
        return bug;
    }

    public String getScriptName() {
        return SCRIPT_NAME;
    }
}

