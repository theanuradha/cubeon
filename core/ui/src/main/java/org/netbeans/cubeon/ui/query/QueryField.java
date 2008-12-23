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
 *
 * @author g.hartmann
 */
public class QueryField implements Comparable<QueryField> {

    public enum Type { TEXT, TEXTAREA, RADIO, CHECKBOX, SELECT };

    private final String name;
    private final String display;
    private final Type   type;
    private final int    order;
    private final Set<?> options;

    public QueryField(String name, String display, Type type, int order,
            Set<?> options) {
        if (name == null)
            throw new NullPointerException("name must not be null");

        this.name = name;
        this.display = display;
        this.type = type;
        this.order = order;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public Type getType() {
        return type;
    }

    public int getOrder() {
        return order;
    }

    public Set<?> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueryField other = (QueryField) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.display == null) ? (other.display != null) : !this.display.equals(other.display)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.order != other.order) {
            return false;
        }
        if (this.options != other.options && (this.options == null || !this.options.equals(other.options))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.display != null ? this.display.hashCode() : 0);
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + this.order;
        hash = 67 * hash + (this.options != null ? this.options.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("QueryField[");
        buffer.append(name).append(",")
                .append(display).append(",")
                .append(type).append(",")
                .append(order).append(",")
                .append(options).append("]");
        return buffer.toString();
    }

    @Override
    public int compareTo(QueryField other) {
        return order - other.order;
    }

}
