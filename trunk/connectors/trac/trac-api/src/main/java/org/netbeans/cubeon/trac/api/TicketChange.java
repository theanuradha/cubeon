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

/**
 *
 * @author Anuradha
 */
public class TicketChange {
   
    private final long time;
    private final String author;
    private final String field;
    private final String oldValue;
    private final String newValuve;

    public TicketChange(long time, String author, String field,
            String oldValue, String newValuve) {
        this.time = time;
        this.author = author;
        this.field = field;
        this.oldValue = oldValue;
        this.newValuve = newValuve;
    }

    public String getAuthor() {
        return author;
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

    public long getTime() {
        return time;
    }


    @Override
    public String toString() {
        return new StringBuilder().append(time).
                append(":").append(author).append(":").append(field).append(":").
                append(oldValue).append(" => ").append(newValuve).toString();
    }
}
