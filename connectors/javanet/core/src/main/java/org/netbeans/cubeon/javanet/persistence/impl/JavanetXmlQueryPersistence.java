/*
 *  Copyright 2009 Tomas Knappek.
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

import org.netbeans.cubeon.javanet.persistence.JavanetQueryPersistence;
import org.netbeans.cubeon.javanet.query.JavanetRemoteQuery;
import org.w3c.dom.Element;

/**
 *
 * @author Tomas Knappek
 */
public class JavanetXmlQueryPersistence extends AbstractXmlPersistence<JavanetRemoteQuery> implements JavanetQueryPersistence {

    private static final String TAG_ROOT = "javanet-queries";
    private static final String TAG_QUERIES = "queries";
    private static final String TAG_QUERY = "query";
    private static final String TAG_NAME = "name";
    private static final String TAG_PROJECT = "project";

    @Override
    protected String getFileName() {
        return "queries.xml";
    }

    @Override
    protected String getBaseDir() {
        return "cubeon/javanet_queries";
    }

    @Override
    protected String getTagRoot() {
        return TAG_ROOT;
    }

    @Override
    protected String getTagElements() {
        return TAG_QUERIES;
    }

    @Override
    protected String getTagElement() {
        return TAG_QUERY;
    }

    @Override
    protected JavanetRemoteQuery mapToObject(Element e) {
        String name = e.getAttribute(TAG_NAME);
        String project = e.getAttribute(TAG_PROJECT);
        JavanetRemoteQuery query = new JavanetRemoteQuery();
        query.setName(name);
        query.setProjectName(project);
        return query;

    }

    @Override
    protected Element mapToElement(JavanetRemoteQuery q) {
        Element element = getDocument().createElement(getTagElement());
        element.setAttribute(TAG_ID, q.getId());
        element.setAttribute(TAG_NAME, q.getName());
        element.setAttribute(TAG_PROJECT, q.getTaskRepository().getId());
        return element;
    }

}
