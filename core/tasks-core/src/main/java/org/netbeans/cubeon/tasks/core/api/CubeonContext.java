/*
 *  Copyright 2008 Cube°n Team.
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

import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public interface CubeonContext {

    Lookup getLookup();

    void setActive(TaskElement task);

    TaskElement getActive();

    void addContextListener(CubeonContextListener l);

    void removeContextListener(CubeonContextListener l);
}
