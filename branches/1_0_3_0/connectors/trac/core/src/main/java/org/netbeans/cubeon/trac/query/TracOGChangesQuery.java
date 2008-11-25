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
package org.netbeans.cubeon.trac.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.repository.TracTaskRepository;
import org.netbeans.cubeon.trac.tasks.TracTask;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class TracOGChangesQuery extends AbstractTracQuery {

    private AtomicBoolean inezilized = new AtomicBoolean(true);
    private Set<String> ids = new HashSet<String>();

    public TracOGChangesQuery(TracTaskRepository repository) {
        super(repository, "_outgoing");//NOI18N
    }

    @Override
    public Type getType() {
        return Type.UTIL;
    }

    public String getName() {
        return NbBundle.getMessage(TracOGChangesQuery.class, "LBL_Outgoing_Tasks");
    }

    public String getDescription() {
        return NbBundle.getMessage(TracOGChangesQuery.class, "LBL_Outgoing_Task_dec",getTaskRepository().getName());
    }

    public Lookup getLookup() {
        return Lookups.fixed(this, extension);
    }

    public void synchronize() {
        final TracTaskRepository repository = getRepository();


        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                synchronized (TracOGChangesQuery.this) {
                    extension.fireSynchronizing();
                    ProgressHandle handle = ProgressHandleFactory.createHandle("Synchronizing Query : " + getName());
                    handle.start();
                    handle.switchToIndeterminate();
                    ids.clear();
                    try {
                    } finally {
                        List<String> taskIds = repository.getTaskIds();
                        for (String id : taskIds) {
                            TracTask jt = repository.getTaskElementById(id);
                            if (jt!=null && jt.isModifiedFlag()) {
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
        TracTaskRepository repository = getRepository();
        for (String key : ids) {
            TaskElement element = repository.getTaskElementById(key);
            if (element != null) {
                elements.add(element);
            }
        }

        return elements;
    }

    public void removeTaskId(String id) {
        ids.remove(id);
        extension.fireSynchronized();
    }

    public void addTaskId(String id) {
        ids.add(id);
        extension.fireSynchronized();
    }
}
