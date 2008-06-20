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
package org.netbeans.cubeon.tasks.spi;

import org.netbeans.cubeon.tasks.spi.repository.TaskRepository;
import org.netbeans.cubeon.tasks.spi.priority.TaskPriority;
import java.awt.Image;
import java.net.URL;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public interface TaskElement {

    /**
     * 
     * @return
     */
    String getId();

    /**
     * 
     * @return
     */
    String getName();

    /**
     * 
     * @return
     */
    String getDescription();

    /**
     * 
     * @return
     */
    TaskRepository getTaskRepository();

    /**
     * 
     * @return
     */
    Lookup getLookup();

    /**
     * 
     * @return
     */
    boolean isCompleted();

    /**
     * 
     * @return
     */
    TaskPriority getPriority();

    /**
     * 
     * @param priority
     */
    void setPriority(TaskPriority priority);

    /**
     * 
     * @return
     */
    TaskStatus getStatus();

    /**
     * 
     * @param status
     */
    void setStatus(TaskStatus status);

    /**
     * 
     * @return
     */
    TaskType getType();

    /**
     * 
     * @param type
     */
    void setType(TaskType type);
    
    
    /**
     * 
     * @return
     */
    Image getImage();
    
    /**
     * 
     * @return
     */
    URL getUrl();
}
