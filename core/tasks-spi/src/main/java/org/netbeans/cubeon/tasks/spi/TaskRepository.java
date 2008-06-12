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

import java.awt.Image;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public interface TaskRepository {

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
    Lookup getLookup();

    /**
     * 
     * @return
     */
    TaskElement createTaskElement();

    /**
     * 
     * @return
     */
    List<TaskElement> getTaskElements();

    /**
     * 
     * @param id
     * @return
     */
    TaskElement getTaskElementById(String id);

    /**
     * save modifid attributes
     * @param element 
     */
    void persist(TaskElement element);

    /**
     * reset modifid attributes
     * @param element 
     */
    void reset(TaskElement element);
    
    Image getImage();
}