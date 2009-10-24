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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Anuradha
 */
public class JSONUtils {

    private JSONUtils() {
    }

    public static long getLongValue(JSONObject jSONObject, String key) {
        Object value = jSONObject.get(key);
        if (value instanceof Long) {
            return ((Long) value).longValue();
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException numberFormatException) {
                //ignore
            }
        }
        return 0;
    }

    public static int getIntValue(JSONObject jSONObject, String key) {
        Object value = jSONObject.get(key);
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException numberFormatException) {
                //ignore
            }
        }
        return 0;
    }

    public static boolean getBooleanValue(JSONObject jSONObject, String key) {
        Object value = jSONObject.get(key);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }

        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false;
    }

    public static List<String> getStrings(JSONObject jSONObject, String key) {
        List<String> strings = new ArrayList<String>();
        Object value = jSONObject.get(key);
        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            for (Object object : array) {
                strings.add((String) object);
            }
        }
        return strings;
    }
}
