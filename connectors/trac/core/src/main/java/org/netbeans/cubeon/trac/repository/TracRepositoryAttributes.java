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
package org.netbeans.cubeon.trac.repository;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.cubeon.trac.api.TicketFiled;
import org.netbeans.cubeon.trac.api.TracException;

/**
 *
 * @author Anuradha G
 */
public class TracRepositoryAttributes {

    private final TracTaskRepository repository;
    private List<TicketFiled> ticketFileds = new ArrayList<TicketFiled>(0);
    private final TracAttributesPersistence persistence;
    private final Object LOCK = new Object();

    public TracRepositoryAttributes(TracTaskRepository repository) {
        this.repository = repository;
        persistence = new TracAttributesPersistence(this, repository.getBaseDir());
    }

    public TicketFiled getTicketFiledByName(String name) {
        //iterate and find matching TicketFiled from list
        for (TicketFiled ticketFiled : ticketFileds) {
            if (name.equals(ticketFiled.getName())) {
                return ticketFiled;
            }
        }
        //if TicketFiled not found return null
        return null;
    }

    public List<TicketFiled> getTicketFileds() {
        return new ArrayList<TicketFiled>(ticketFileds);
    }

    void setTicketFileds(List<TicketFiled> ticketFileds) {
        this.ticketFileds = new ArrayList<TicketFiled>(ticketFileds);
    }

    public TracTaskRepository getRepository() {
        return repository;
    }

    void refresh(ProgressHandle progressHandle) throws TracException {
        synchronized (LOCK) {

            persistence.refresh(progressHandle);

        }
    }

    public void loadAttributes() {
        persistence.loadAttributes();
    }
}
