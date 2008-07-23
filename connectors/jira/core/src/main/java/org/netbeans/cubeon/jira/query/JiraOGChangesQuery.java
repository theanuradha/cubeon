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
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.cubeon.jira.repository.JiraTaskRepository;
import org.netbeans.cubeon.jira.tasks.JiraTask;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class JiraOGChangesQuery extends AbstractJiraQuery {

    private AtomicBoolean inezilized = new AtomicBoolean(true);
    private List<String> ids = new ArrayList<String>();

    public JiraOGChangesQuery(JiraTaskRepository repository) {
        super(repository, "_outgoing");
    }

    @Override
    public Type getType() {
        return Type.UTIL;
    }

    public String getName() {
        return "Outgoing Tasks";
    }

    public String getDescription() {
        return "Query that show all task that contain outgoing changes";
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, extension);
    }

    public void synchronize() {
        final JiraTaskRepository repository = getRepository();


        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                synchronized (JiraOGChangesQuery.this) {
                    extension.fireSynchronizing();
                    ProgressHandle handle = ProgressHandleFactory.createHandle("Synchronizing Query : " + getName());
                    handle.start();
                    handle.switchToIndeterminate();
                    ids.clear();
                    try {
                    } finally {
                        List<String> taskIds = repository.getTaskIds();
                        for (String id : taskIds) {
                            JiraTask jt = repository.getTaskElementById(id);
                            if (jt.isModifiedFlag()) {
                                ids.add(id);
                            }
                        }

                        handle.finish();
                        extension.fireSynchronized();
                    }
                }

            }
        });

    }

    public List<TaskElement> getTaskElements() {
        if (inezilized.getAndSet(false)) {
            synchronize();
        }
        List<TaskElement> elements = new ArrayList<TaskElement>();
        JiraTaskRepository repository = getRepository();
        for (String key : ids) {
            TaskElement element = repository.getTaskElementById(key);
            if (element != null) {
                elements.add(element);
            }
        }

        return elements;
    }
}
