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
package org.netbeans.cubeon.jira.query;

import java.util.List;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha
 */
public class JiraFilterQuery extends AbstractJiraQuery {

    private String filterId;

    public JiraFilterQuery(JiraTaskRepository repository, String id, String filterId) {
        super(repository, id);
        this.filterId = filterId;

    }

    @Override
    public Type getType() {
        return Type.FILTER;
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Lookup getLookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void synchronize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskElement> getTaskElements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
 }
