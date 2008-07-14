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
import org.netbeans.cubeon.tasks.spi.task.TaskContainer;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;

import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public interface TaskFolder extends TaskContainer {



    /**
     * 
     * @return Name of the folder
     */
    String getName();

    /**
     * 
     * @return Description of folder
     */
    String getDescription();

    /**
     * 
     * @return Parent Folder 
     */
    TaskFolder getParent();

    /**
     * 
     * @return Lookup instance contain feature provided like  
     * @link TaskFolderOparations, Node 
     */
    Lookup getLookup();

   


    List<TaskFolder> getSubFolders();

 

    List<TaskElement> getTaskElements();

    boolean contains(TaskElement element);



    TaskQuery getTaskQuery();
    
}
