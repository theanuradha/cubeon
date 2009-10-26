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

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author Anuradha
 */
public class QueryInfo implements JSONAware {

    private String id;
    private String name;
    private String query;
    private List<String> ids = new ArrayList<String>();

    public String toJSONString() {
        JSONObject jsono = new JSONObject();
        jsono.put("id", id);
        jsono.put("name", name);
        jsono.put("query", query);
        jsono.put("task-ids", ids);
        return jsono.toJSONString();
    }

    public static QueryInfo toQueryInfo(JSONObject jsono) {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.id = (String) jsono.get("id");
        queryInfo.name = (String) jsono.get("name");
        queryInfo.query = (String) jsono.get("query");
        queryInfo.ids = JSONUtils.getStrings(jsono, "task-ids");
        return queryInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
