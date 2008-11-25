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
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Anuradha
 */
public class TicketChange {

    private final long time;
    private final String author;
    private  String comment;
    private List<FieldChange> fieldChanges = new ArrayList<FieldChange>();

    public TicketChange(long time, String author) {
        this.time = time;
        this.author = author;
    }


    public TicketChange(long time, String author, String comment) {
        this.time = time;
        this.author = author;
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public long getTime() {
        return time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldChange> getFieldChanges() {
        return new ArrayList<FieldChange>(fieldChanges);
    }

    public void setFieldChanges(List<FieldChange> fieldChanges) {
        this.fieldChanges = new ArrayList<FieldChange>(fieldChanges);
    }

    public boolean removeFieldChange(FieldChange change) {
        return fieldChanges.remove(change);
    }

    public boolean addAllFieldChanges(Collection<FieldChange> changes) {
        return fieldChanges.addAll(changes);
    }

    public boolean addFieldChange(FieldChange change) {
        return fieldChanges.add(change);
    }

    @Override
    public String toString() {
        return new StringBuilder().append(time).
                append(":").append(author).append(":").append(comment).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TicketChange other = (TicketChange) obj;
        if (this.time != other.time) {
            return false;
        }
        if ((this.author == null) ? (other.author != null) : !this.author.equals(other.author)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (int) (this.time ^ (this.time >>> 32));
        hash = 73 * hash + (this.author != null ? this.author.hashCode() : 0);
        return hash;
    }

    public static class FieldChange {

        private final String field;
        private final String oldValue;
        private final String newValuve;

        public FieldChange(String field, String oldValue, String newValuve) {
            this.field = field;
            this.oldValue = oldValue;
            this.newValuve = newValuve;
        }

        public String getField() {
            return field;
        }

        public String getNewValuve() {
            return newValuve;
        }

        public String getOldValue() {
            return oldValue;
        }

        @Override
        public String toString() {
            return new StringBuilder().append(field).append(":").
                    append(oldValue).append(" => ").append(newValuve).toString();
        }
    }
}
