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

/**
 *
 * @author Anuradha
 */
public interface TaskEditorProvider {

    /**
     * 
     * @return
     */
    BasicAttributeHandler createBasicAttributeHandler();

    /**
     * 
     * @return
     */
    EditorAttributeHandler createEditorAttributeHandler();

    /**
     * 
     */
    public interface BasicAttributeHandler {

        /**
         * 
         * @param changeListener
         */
        void addChangeListener(ChangeListener changeListener);

        /**
         * 
         * @param changeListener
         */
        void removeChangeListener(ChangeListener changeListener);

        /**
         * 
         * @return
         */
        boolean isValidConfiguration();

        /**
         * 
         * @return
         */
        JComponent getComponent();

        /**
         * 
         * @return
         */
        TaskElement getTaskElement();
    }

    /**
     * 
     */
    public interface EditorAttributeHandler {

        /**
         * 
         * @return
         */
        String getName();

        /**
         * 
         * @return
         */
        String getDisplayName();

        /**
         * 
         * @return
         */
        String getShortDescription();

        /**
         * 
         * @param changeListener
         */
        void addChangeListener(ChangeListener changeListener);

        /**
         * 
         * @param changeListener
         */
        void removeChangeListener(ChangeListener changeListener);

        /**
         * 
         * @return
         */
        JComponent getComponent();

        /**
         * 
         * @return
         */
        TaskElement save();
    }
}
