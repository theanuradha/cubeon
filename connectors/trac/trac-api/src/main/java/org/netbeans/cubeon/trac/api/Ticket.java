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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static org.netbeans.cubeon.trac.api.TracKeys.*;

/**
 *
 * @author Anuradha G
 */
public class Ticket {

    private int id;
    private final Map<String, String> map = new HashMap<String, String>();
    private long createdDate = 0;
    private long updatedDate = 0;
    private List<TicketChange> changes = new ArrayList<TicketChange>();

    public Ticket(int id, String summary, String description) {
        this.id = id;
        put(SUMMARY, summary);
        put(DESCRIPTION, description);
    }

    public Ticket(int id) {
        this.id = id;
    }

    public int getTicketId() {
        return id;
    }

    public void setTicketId(int id) {
        this.id = id;
    }

    public String getSummary() {
        return get(SUMMARY);
    }

    public void setSummary(String summary) {
        put(SUMMARY, summary);
    }

    public String getDescription() {
        return get(DESCRIPTION);
    }

    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }

    /**
     *
     * @param key key whose mapping is to be removed from the ticket
     * 
     */
    public void remove(Object key) {
        map.remove(key);
    }

    /**
     *
     * @param m mappings to be stored in this ticket
     */
    public void putAll(Map<String, String> m) {
        map.putAll(m);
    }

    /**
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * 
     */
    public void put(String key, String value) {
        if (value == null) {
            //remove if valuve null
            map.remove(key);
        } else {
            map.put(key, value);
        }
    }

    /**
     *
     * @return a set view of the keys contained in this ticket
     */
    public Set<String> keySet() {
        return map.keySet();
    }

    /**
     *
     * @param  key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this ticket contains no mapping for the key
     */
    public String get(String key) {
        return map.get(key);
    }

    /**
     *
     * @return a set view of the mappings contained in this Ticket
     */
    public Set<Entry<String, String>> entrySet() {
        return map.entrySet();
    }

    /**
     * Validate Is givern Key Contains
     * @param key key the key whose associated value is to be returned
     * @return <tt>true</tt> if this ticket contains a mapping for the specified
     *         key
     */
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    /**
     * Removes all of the mappings from this ticket
     */
    public void clear() {
        map.clear();
    }

    public Map<String, String> getAttributes() {
        return new HashMap<String, String>(map);
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean removeTicketChange(TicketChange change) {
        return changes.remove(change);
    }

    public boolean addTicketChange(TicketChange change) {
        return changes.add(change);
    }

    public void setTicketChanges(List<TicketChange> changes) {
        this.changes=new ArrayList<TicketChange>(changes);
    }

    public List<TicketChange> getTicketChanges() {
        return new ArrayList<TicketChange>(changes);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        return buffer.append(id).append(" : ").
                append(getSummary()).append(" : ").
                append(getDescription()).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ticket other = (Ticket) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.id;
        return hash;
    }
}
