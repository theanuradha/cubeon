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

package org.netbeans.cubeon.trac.repository;

import org.netbeans.cubeon.tasks.spi.repository.OfflineTaskSupport;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.trac.tasks.TracTask;

/**
 *
 * @author Anuradha
 */
public class TracOfflineTaskSupport implements OfflineTaskSupport{

    private final TracTaskRepository repository;

    public TracOfflineTaskSupport(TracTaskRepository repository) {
        this.repository = repository;
    }
    

    public boolean hasIncomingChanges(TaskElement element) {
        //TODO : support incoming changes detection
        return false;
    }

    public boolean hasOutgoingChanges(TaskElement element) {
         TracTask tracTask = element.getLookup().lookup(TracTask.class);
        assert tracTask != null;
        return tracTask.isModifiedFlag() || tracTask.isLocal();
    }

}
