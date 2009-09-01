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

import org.netbeans.cubeon.trac.api.TracClient;
import org.netbeans.cubeon.trac.api.TracException;
import org.netbeans.cubeon.trac.api.TracSession;

/**
 *
 * @author Anuradha
 */
public class TracXmlRpcClient implements TracClient {

    /**
     *  Create xmlrpc base trac session
     * @param url
     * @param user
     * @param password
     * @return
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public TracSession createTracSession(String url, String user, String password) throws TracException {
        return new XmlRpcTracSession(url, user, password);
    }
    /**
     *  Create xmlrpc base trac session
     * @param url
     * @param user
     * @param password
     * @param ignoreSSL
     * @return
     * @throws org.netbeans.cubeon.trac.api.TracException
     */
    public TracSession createTracSession(String url, String user, String password,boolean ignoreSSL) throws TracException {
        return new XmlRpcTracSession(url, user, password,ignoreSSL);
    }
}
