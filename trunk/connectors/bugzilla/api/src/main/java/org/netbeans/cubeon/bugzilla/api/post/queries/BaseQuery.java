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

import org.netbeans.cubeon.bugzilla.api.exception.BugzillaException;

import java.util.Map;

/**
 * Base class for all bugs list queries.
 *
 * @author radoslaw.holewa
 */
public abstract class BaseQuery {

    /**
     * Query type.
     */
    private Type type;

    /**
     * One-argument constructor, it needs query type as a parameter.
     *
     * @param type - type of query
     */
    protected BaseQuery(Type type) {
        this.type = type;
    }

    /**
     * Returns query parameters map, these parameters will be used to query bugs.
     *
     * @return - map with query parameters
     */
    public abstract Map<String, String> parametersMap() throws BugzillaException;

    /**
     * Returns type of query.
     *
     * @return - type of query
     */
    public Type getType() {
        return type;
    }

    /**
     * Query type enumeration.
     */
    public enum Type {
        //the simplest query type enum

        SPECIFIC,
        //advanced query type enum
        ADVANCED
    }
}
