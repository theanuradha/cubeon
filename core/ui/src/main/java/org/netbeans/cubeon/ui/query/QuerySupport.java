/*
 *  Copyright 2008 g.hartmann.
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

package org.netbeans.cubeon.ui.query;

import java.util.List;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;

/**
 *
 * @param <T>
 * @author g.hartmann
 */
public interface QuerySupport<T extends TaskQuery> {

    /**
     * Gets all queryable fields.
     * 
     * @return the queryable fields
     */
    public List<QueryField> getQueryFields();

    /**
     * Sets the native query from the supplied filters.
     *
     * @param query the task query
     * @param filters the filters
     */
    public void setQueryFromFilters(T query, List<QueryFilter> filters);

    /**
     * Creates the filters for the supplied native query.
     * 
     * @param query the task query
     * @return the filters
     */
    public List<QueryFilter> createFiltersFromQuery(T query);

    /**
     * Creates a new query.
     *
     * @return query
     */
    public T createQuery();

    /**
     * Sets the name of the supplied query.
     *
     * @param query the query
     * @param name the name
     */
    public void setQueryName(T query, String name);
}
