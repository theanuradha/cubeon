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

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public interface TaskElement {

    String getId();

    String getName();

    String getDescription();

    TaskRepository getTaskRepository();

    Lookup getLookup();

    public void open();
    
    /*--------------------------Priority------------------------*/
    TaskPriority getPriority();

    void setPriority(TaskPriority priority);
    /*-----------------------------------------------------------*/

    /*-----------------------Customizer--------------------------*/
    TaskBasicAttributeHandler createAttributeHandler();

    public interface TaskBasicAttributeHandler {

        void addChangeListener(ChangeListener changeListener);

        void removeChangeListener(ChangeListener changeListener);

        boolean isValidConfiguration();

        JComponent getComponent();

        TaskElement getTaskElement();
    }
}
