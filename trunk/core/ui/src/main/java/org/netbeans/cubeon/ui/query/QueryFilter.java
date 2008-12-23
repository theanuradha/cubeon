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

import java.util.Set;

/**
 * The QueryFilter class represents a filter for a query field.
 * The field value is checked against the filter values using
 * match type. All values are combined with the logical OR
 * operator.
 * 
 * @author g.hartmann
 */
public class QueryFilter implements Comparable<QueryFilter> {

    /*
     * This enum represents the match type for a field value.
     */
    public enum Match {
        IS,             /** field is exact value */
        IS_NOT,         /** field is not value */
        CONTAINS,       /** field contains value */
        CONTAINS_NOT,   /** field doesn't contain value */
        STARTS_WITH,    /** field starts with value */
        ENDS_WITH       /** field ends with value */
    };

    private final QueryField field;
    private final Match      match;
    private final Set<?> values;

    public QueryFilter(QueryField field, Match match, Set<?> values) {
        if (field == null)
            throw new NullPointerException("field must not be null");
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("no values supplied");

        this.field = field;
        this.match = match;
        this.values = values;
    }

    public QueryField getField() {
        return field;
    }

    public Match getMatch() {
        return match;
    }

    public Set<?> getValues() {
        return values;
    }

    public static Match getDefaultMatchForType(QueryField.Type type) {
        switch (type) {
            case TEXT:
            case TEXTAREA:
                return Match.CONTAINS;
            default:
                return Match.IS;
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("QueryFilter[");
        buffer.append(field.getName())
                .append(" ")
                .append(match)
                .append(" ")
                .append(values)
                .append("]");
        return buffer.toString();
    }

    @Override
    public int compareTo(QueryFilter other) {
        return field.compareTo(other.field);
    }

}
