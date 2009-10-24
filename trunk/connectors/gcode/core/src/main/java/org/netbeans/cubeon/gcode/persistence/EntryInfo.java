/*
 *  Copyright 2009 Anuradha.
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
package org.netbeans.cubeon.gcode.persistence;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author Anuradha
 */
public class EntryInfo implements JSONAware {

    private String id;
    private String value;

    public EntryInfo() {
    }

    public EntryInfo(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String toJSONString() {
        JSONObject jsono = new JSONObject();
        jsono.put("id", id);
        jsono.put("value", value);
        return jsono.toJSONString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntryInfo other = (EntryInfo) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "EntryInfo [" + "key " + id + " " + "value " + value + "]";
    }

    public static EntryInfo toEntryInfo(JSONObject jsono) {
        return new EntryInfo((String) jsono.get("id"), (String) jsono.get("value"));
    }
}
