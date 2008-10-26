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

import org.apache.commons.httpclient.methods.PostMethod;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaParsingException;

/**
 * Base class for all Bugzilla POST methods.
 *  
 * @author radoslaw.holewa
 */
public abstract class BaseBugzillaPostMethod<T> extends PostMethod {

    /**
     * Constructor of the base Bugzilla POST method.
     *
     * @param url - Bugzilla repository URL address
     */
    public BaseBugzillaPostMethod(String url) {
        super();
        setPath(getScriptUrl(url));
    }

    /**
     * Returns URL address of script with the name returned by method
     * {@see org.netbeans.cubeon.bugzilla.api.post.method.BaseBugzillaPostMethod#getScriptName()} method.
     *
     * @param url - repository URL
     * @return - returned script URL
     */
    private String getScriptUrl(String url) {
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url + getScriptName();
    }

    /**
     * Returns object representation of returned XML content.
     *
     * @return - object representation of returned XML content.
     * @throws BugzillaParsingException - throws exception in case there were problems during XML parsing
     */
    public abstract T getResult() throws BugzillaParsingException;

    /**
     * Returns name of script which will be used to get XML content for selected method.
     *
     * @return - script name
     */
    public abstract String getScriptName();
}
