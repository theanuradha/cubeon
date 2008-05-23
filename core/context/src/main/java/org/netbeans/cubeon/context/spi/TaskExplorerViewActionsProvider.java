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
package org.netbeans.cubeon.context.spi;

import javax.swing.Action;

/**
 *
 * @author Anuradha G
 */
public interface TaskExplorerViewActionsProvider {

    /**
     * Use to sort TaskExplorerViewActionsProvider 
     * @return Number 
     */
    int getPosition();

    /**
     * New Menu Actions
     * @return Actions that show in  tasks view. Can return null elements 
     * to add sepetator to menu 
     */
    Action[] getNewActions();

    /**
     * Repositort View Menu Actions
     * 
     * @return Actions that show in top level
     */
    Action[] getActions();
}
