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
import org.netbeans.cubeon.bugzilla.api.util.ProgressMonitorInputStream;
import org.netbeans.cubeon.bugzilla.api.util.ProgressMonitor;

import java.io.InputStream;
import java.io.IOException;

/**
 * Base class for all Bugzilla POST methods.
 *
 * @author radoslaw.holewa
 */
public abstract class BaseBugzillaPostMethod<T> extends PostMethod {

    /**
     * Optional progress monitor. 
     */
    private ProgressMonitor progressMonitor;

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
     * Two-arguments constructor, it will be used to create instance of POST method that will provide
     * response InputStream with given ProgressMonitor.
     * @param url - Bugzilla repository URL address
     * @param progressMonitor - ProgressMonitor that will be used to monitor input stream
     */
    public BaseBugzillaPostMethod( String url, ProgressMonitor progressMonitor ) {
      super( url );
      this.progressMonitor = progressMonitor;
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
     * Returns response body, if ProgressMonitor is provided then it will return ProgressMonitorInputStream instance
     * which will monitor stream state. 
     * @return - response input stream
     * @throws IOException - throws exception incase there are any errors 
     */
    public InputStream getResponseBodyAsStream() throws IOException {
      InputStream inputStream = null;
      if(progressMonitor != null) {
        inputStream = new ProgressMonitorInputStream(super.getResponseBodyAsStream(), null);
      } else {
        inputStream = super.getResponseBodyAsStream();
      }
      return inputStream;
    }

  /**
     * Returns name of script which will be used to get XML content for selected method.
     *
     * @return - script name
     */
    public abstract String getScriptName();
}
