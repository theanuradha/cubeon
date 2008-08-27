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
package org.netbeans.cubeon.trac.internals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.netbeans.cubeon.trac.api.TracException;
import org.netbeans.cubeon.trac.api.TracSession;

/**
 *
 * @author Anuradha G
 */
public class XmlRpcTracSession implements TracSession {

    private final XmlRpcClient client;

    /**
     * Create XmlRpcTracSession for trac 
     * @param url
     * @param user
     * @param password
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    XmlRpcTracSession(String url, String user, String password) throws TracException {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            //append "/login/xmlrpc" to url 
            config.setServerURL(new URL(url + "/login/xmlrpc"));//NOI18N
            //user name and pass to trac
            //TODO may be need to annnonymous 
            config.setBasicUserName(user);
            config.setBasicPassword(password);
            client = new XmlRpcClient();
            client.setConfig(config);
            // validate connitivity and api version
            /**
             * Returns a list with three elements.
             * First element is the epoch (0=Trac 0.10, 1=Trac 0.11 or higher).
             * Second element is the major version number, third is the minor.
             * Changes to the major version indicate API breaking changes,
             * while minor version changes are simple additions, bug fixes, etc.
             */
            Object[] versionInfo = (Object[]) client.execute("system.getAPIVersion()",
                    new Object[0]);//NOI18N
        //TODO validate trac version using versionInfo
        } catch (XmlRpcException ex) {
            throw new TracException(ex);
        } catch (MalformedURLException ex) {
            throw new TracException(ex);
        }
    }

    /**
     * Create Multi call Element
     * @param methodName
     * @param parameters
     * @return 
     */
    private Map<String, Object> _createMultiCallElement(String methodName, Object... parameters) {
        Map<String, Object> table = new HashMap<String, Object>();
        table.put("methodName", methodName);//NOI18N
        table.put("params", parameters);//NOI18N
        return table;
    }
}
