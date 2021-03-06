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
 * @author Anuradha G
 */
public class TicketComponent {
    //{description, name, owner}

    private final String name;
    private final String description;
    private final String owner;

    public TicketComponent(String name, String description, String owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return name;
    }
}
