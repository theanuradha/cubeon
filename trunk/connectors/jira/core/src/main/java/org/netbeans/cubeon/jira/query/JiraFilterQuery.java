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

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.repository.attributes.JiraFilter;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JiraFilterQuery extends AbstractJiraQuery {

    private JiraFilter filter;
   

    public JiraFilterQuery(JiraTaskRepository repository, String id) {
        super(repository, id);
    }
    
    @Override
    public Type getType() {
        return Type.FILTER;
    }

    public String getName() {
        return filter.getName();
    }

    public String getDescription() {
        return filter.getDescription();
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, extension);
    }

    public void synchronize() {
        //TODO
    }

    public List<TaskElement> getTaskElements() {
        List<TaskElement> elements = new ArrayList<TaskElement>();

        return elements;
    }

    public JiraFilter getFilter() {
        return filter;
    }

    public void setFilter(JiraFilter filter) {
        this.filter = filter;
    }
}
