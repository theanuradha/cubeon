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
package org.netbeans.cubeon.tasks.core.internals;

import java.util.Comparator;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;

/**
 *
 * @author Anuradha
 */
class TaskFolderComparator implements Comparator<TaskFolder> {

    TaskFolderComparator() {
    }

    public int compare(TaskFolder o1, TaskFolder o2) {


        return o1.getName().compareTo(o2.getName());
    }
}
