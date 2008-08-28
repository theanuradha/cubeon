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
package org.netbeans.cubeon.trac.api;

import java.util.List;

/**
 *{optional, name, value, label, type, options}
 * @author Anuradha
 */
public class TicketFiled {

    private final String name;
    private final String label;
    private final String value;
    private final String type;
    private final boolean optional;
    private final List<String> options;

    public TicketFiled(String name, String label, String value, String type,
            boolean optional, List<String> options) {
        this.name = name;
        this.label = label;
        this.value = value;
        this.type = type;
        this.optional = optional;
        this.options = options;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return optional;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuffer buffer =
                new StringBuffer("{optional, name, value, label, type, options}=");
        buffer.append(name).append(",");
        buffer.append(label).append(",");
        buffer.append(type).append(",");
        buffer.append(value).append(",");
        buffer.append(optional).append(",");
        buffer.append(options).append(",");
        return buffer.toString();
    }
}
