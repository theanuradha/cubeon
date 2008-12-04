/*
 *  Copyright 2008 Tomas Knappek.
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
package org.netbeans.cubeon.javanet.persistence.impl;

import org.netbeans.cubeon.javanet.persistence.JavanetRepoPersistence;
import org.netbeans.cubeon.javanet.repository.JavanetTaskRepository;
import org.netbeans.cubeon.tasks.core.api.RepositoryUtils;
import org.w3c.dom.Element;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetXmlRepoPersistence extends AbstractXmlPersistence<JavanetTaskRepository> implements JavanetRepoPersistence {

    private static final String TAG_ROOT = "javanet-repositories";//NOI18N
    private static final String TAG_REPOSITORIES = "repositories";//NOI18N
    private static final String TAG_REPOSITORY = "repository";//NOI18N
    private static final String TAG_NAME = "name";//NOI18N
    private static final String TAG_USERID = "userid";//NOI18N
    private static final String TAG_PASSWORD_HASH = "password";//NOI18N

    @Override
    protected String getFileName() {
        return "repositories.xml";
    }

    @Override
    protected String getBaseDir() {
        return "cubeon/javanet_repositories";
    }

    @Override
    protected String getTagRoot() {
        return TAG_ROOT;
    }

    @Override
    protected String getTagElements() {
        return TAG_REPOSITORIES;
    }

    @Override
    protected String getTagElement() {
        return TAG_REPOSITORY;
    }

    @Override
    protected JavanetTaskRepository mapToObject(Element element) {
        String id = element.getAttribute(TAG_ID);        
        String name = element.getAttribute(TAG_NAME);                
        String user = element.getAttribute(TAG_USERID);
        String passwordHash = element.getAttribute(TAG_PASSWORD_HASH);
        String password = RepositoryUtils.decodePassword(user, passwordHash);

        JavanetTaskRepository repo = null;
        if (name != null && user != null && password != null) {
            repo = new JavanetTaskRepository(null, name, user, password);
        }
        return repo;
    }

    @Override
    protected Element mapToElement(JavanetTaskRepository t) {
        Element element = getDocument().createElement(getTagElement());
        element.setAttribute(TAG_ID, t.getId());
        element.setAttribute(TAG_NAME, t.getName());
        element.setAttribute(TAG_USERID, t.getUserName());
        element.setAttribute(TAG_PASSWORD_HASH, RepositoryUtils.encodePassword(t.getUserName(),
                t.getPassword()));
        return element;
    }
}
