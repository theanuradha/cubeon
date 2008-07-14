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
package org.netbeans.cubeon.tasks.core.api;

import java.util.List;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;

/**
 *
 * @author Anuradha G
 */
public interface TasksFileSystem {

    List<TaskFolder> getFolders();

    TaskFolder getDefaultFolder();

    TaskFolder getRootTaskFolder();

    void addNewFolder(TaskFolder parent, TaskFolder folder);

    boolean removeFolder(TaskFolder parent, TaskFolder folder);

    TaskElement addTaskElement(TaskFolder folder, TaskElement element);

    boolean removeTaskElement(TaskFolder folder, TaskElement element);

    boolean rename(TaskFolder folder, String name, String description);

    void setTaskQuery(TaskFolder folder, TaskQuery query);

    public TaskFolder newFolder(String folderName, String folderDescription);
    //TODO : add backup restore oprations
}
