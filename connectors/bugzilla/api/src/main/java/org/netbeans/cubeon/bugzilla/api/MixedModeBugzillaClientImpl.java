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
package org.netbeans.cubeon.bugzilla.api;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaConnectionException;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaException;
import org.netbeans.cubeon.bugzilla.api.model.BugSummary;
import org.netbeans.cubeon.bugzilla.api.model.BugDetails;
import org.netbeans.cubeon.bugzilla.api.model.RepositoryAttributes;
import org.netbeans.cubeon.bugzilla.api.post.method.GetBugDetailsMethod;
import org.netbeans.cubeon.bugzilla.api.post.method.GetRepositoryConfigurationMethod;
import org.netbeans.cubeon.bugzilla.api.post.method.QueryBugsListPostMethod;
import org.netbeans.cubeon.bugzilla.api.post.queries.BaseQuery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.cubeon.bugzilla.api.model.NewBug;

/**
 * Bugzilla repository client implementation.
 *
 * @author radoslaw.holewa
 */
public class MixedModeBugzillaClientImpl implements BugzillaClient {

    /**
     * Bugzilla XML-RPC script, XML-RPC client has to connect to this script.
     */
    public static final String XML_RPC_SCRIPT = "xmlrpc.cgi";

    /**
     * Character used at the end of URL.
     */
    public static final String URL_END_CHARACTER = "/";

    /**
     * XML-RPC client configuration.
     */
    private final XmlRpcClientConfigImpl config;

    /**
     * XML-RPC client instance.
     */
    private final XmlRpcClient client;

    /**
     * HTTP client instance.
     */
    private final HttpClient httpClient;

    /**
     * User ID.
     */
    private final Integer userId;

    /**
     * URL address that will
     */
    private String url;

    /**
     * Creates instance of MixedMode Bugzilla client.
     *
     * @param url - Bugzilla repository URL address
     * @param user - Bugzilla user
     * @param password - Bugzilla user's password
     * @throws BugzillaException - throws exception in case there are any problems during initialization
     * of Bugzilla client
     */
    public MixedModeBugzillaClientImpl(String url, String user, String password) throws BugzillaException {
        this.url = url;
        config = createConfiguration(url, user, password);
        client = new XmlRpcClient();
        XmlRpcCommonsTransportFactory factory = new XmlRpcCommonsTransportFactory(client);
        /**
         * multi-threaded connection manager - it must be used in case there will be more than one
         * thread that will use HttpClient
         */
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        factory.setHttpClient(httpClient);
        client.setTransportFactory(factory);
        client.setConfig(config);
        Map params = doLogin(user, password);
        if (!params.containsKey("id")) {
            throw new BugzillaException("There is no id property in the response received from login operation!");
        }
        userId = (Integer) params.get("id");
    }

    /**
     * Creates configuration for XML-RPC client.
     *
     * @param url - service url
     * @param user - username
     * @param password - user password
     * @return - configuration
     * @throws BugzillaException - throws exception in case there are any errors
     * during configuration creation
     */
    private XmlRpcClientConfigImpl createConfiguration(String url, String user, String password) throws BugzillaException {
        try {
            XmlRpcClientConfigImpl result = new XmlRpcClientConfigImpl();
            result.setBasicUserName(user);
            result.setBasicPassword(password);
            result.setServerURL(new URL(formatUrlAddress(url)));
            return result;
        } catch (MalformedURLException e) {
            throw new BugzillaConnectionException("Error during configuration initialization.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map doLogin(String user, String password) throws BugzillaException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("login", user);
            params.put("password", password);
            params.put("rememberlogin", "Bugzilla_remember");
            return (HashMap) client.execute("User.login", new Object[]{params});
        } catch (XmlRpcException e) {
            throw new BugzillaConnectionException("Error during login.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public BugDetails getBugDetails(Integer  bugId) throws BugzillaException {
        GetBugDetailsMethod method = new GetBugDetailsMethod(url, bugId);
        try{
            executeMethod( method );
            return method.getResult();
        } finally {
            // we have to release connection after using it
            method.releaseConnection();
        }
    }

    /**
     * {@inheritDoc}
     */
    public RepositoryAttributes getRepositoryAttributes() throws BugzillaException {
        GetRepositoryConfigurationMethod method = new GetRepositoryConfigurationMethod(url);
        try {
            executeMethod( method );
            return method.getResult();
        } finally {
            // we have to release connection after using it
            method.releaseConnection();
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<BugSummary> queryForBugs(BaseQuery query) throws BugzillaException {
        QueryBugsListPostMethod method = new QueryBugsListPostMethod(url, query);
        try {
            executeMethod( method );
            return method.getResult();
        } finally {
            // we have to release connection after using it
            method.releaseConnection();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer createBug(NewBug bug) throws BugzillaException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("product", bug.getProduct());
            params.put("component", bug.getComponent());
            params.put("summary", bug.getSummary());
            params.put("version", bug.getVersion());
            //checking not mandatory properties
            if (bug.getDescription() != null) {
                params.put("description", bug.getDescription());
            }
            if (bug.getOperatingSystem() != null) {
                params.put("op_sys", bug.getOperatingSystem());
            }
            if (bug.getPlatform() != null) {
                params.put("platform", bug.getPlatform());
            }
            if (bug.getPriority() != null) {
                params.put("priority", bug.getPriority());
            }
            if (bug.getSeverity() != null) {
                params.put("severity", bug.getSeverity());
            }
            if (bug.getAlias() != null) {
                params.put("alias", bug.getAlias());
            }
            if (bug.getAssignee() != null) {
                params.put("assigned_to", bug.getAssignee());
            }
            if (bug.getCc() != null && !bug.getCc().isEmpty()) {
                params.put("cc", bug.getCc().toArray());
            }
            if (bug.getQaContact() != null) {
                params.put("qa_contact", bug.getQaContact());
            }
            if (bug.getStatus() != null) {
                params.put("status", bug.getStatus());
            }
            if (bug.getTargetMilestone() != null) {
                params.put("target_milestone", bug.getTargetMilestone());
            }
            Map result = (HashMap) client.execute("Bug.create", new Object[]{params});
            return (Integer) result.get("id");
        } catch (XmlRpcException e) {
            throw new BugzillaConnectionException("Error during bug adding.", e);
        }
    }

    /**
     *  Wraps HTTP method invocation.
     *  This method takes care of connection exceptions and HTTP method results.
     *
     * @param method - HTTP method that will be invoked
     * @throws BugzillaConnectionException - throws exception in case of any errors
     */
    private void executeMethod( HttpMethod method ) throws BugzillaConnectionException {
       try {
            int result = httpClient.executeMethod(method);
            if(result != HttpStatus.SC_OK) {
              throw new BugzillaConnectionException( "Bad HTTP response: " + result);
            }
        } catch (IOException e) {
            throw new BugzillaConnectionException("Error while invoking POST method.", e);
        }
    }

    /**
     * Returns useful form of Bugzilla repository URL.
     * @param url - Bugzilla repository URL
     * @return - useful form of given URL
     */
    private String formatUrlAddress(String url) {
        StringBuilder sb = new StringBuilder(url);
        if (!url.endsWith(URL_END_CHARACTER)) {
            sb.append(URL_END_CHARACTER);
        }
        sb.append(XML_RPC_SCRIPT);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getBugUrl( String id ) {
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url + GetBugDetailsMethod.SHOW_BUG_CGI_SCRIPT + "?id=" + id;
    }
}
