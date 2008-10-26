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
package org.netbeans.cubeon.bugzilla.api.post.queries;

import org.apache.commons.lang.StringUtils;
import org.netbeans.cubeon.bugzilla.api.exception.BugzillaException;

import java.util.HashMap;
import java.util.Map;

/**
 * Specific query, it's the simplest version of bugs list query.
 *
 * @author radoslaw.holewa
 */
public class SpecificQuery extends BaseQuery {

    /**
     * Query format.
     */
    public static final String QUERY_FORMAT = "specific";
    /**
     * Bug status, it's one of query criterions.
     */
    private String bugStatus = "__all__";
    /**
     * Bug content query criterion.
     */
    private String content;
    /**
     * Product name query criterion.
     */
    private String product = StringUtils.EMPTY;

    /**
     * Default constructor.
     */
    public SpecificQuery() {
        super(Type.SPECIFIC);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> parametersMap() throws BugzillaException {
        if (StringUtils.isEmpty(content)) {
            throw new BugzillaException("Content parameter shouldn't be NULL");
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("query_format", QUERY_FORMAT);
        params.put("order", "relevance desc");
        params.put("bug_status", bugStatus);
        params.put("product", product);
        params.put("content", content);
        return params;
    }

    public String getBugStatus() {
        return bugStatus;
    }

    public void setBugStatus(String bugStatus) {
        this.bugStatus = bugStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
