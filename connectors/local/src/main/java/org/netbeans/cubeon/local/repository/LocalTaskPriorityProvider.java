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
package org.netbeans.cubeon.local.repository;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.local.LocalTaskPriority;
import org.netbeans.cubeon.tasks.spi.TaskPriority;
import org.netbeans.cubeon.tasks.spi.TaskPriorityProvider;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class LocalTaskPriorityProvider implements TaskPriorityProvider {

    private static final List<TaskPriority> prioritys = new ArrayList<TaskPriority>();
    public static final LocalTaskPriority P1 = new LocalTaskPriority("P1", -10000,
            Utilities.loadImage("org/netbeans/cubeon/local/p1.png"));
    public static final LocalTaskPriority P2 = new LocalTaskPriority("P2", 1000,
            Utilities.loadImage("org/netbeans/cubeon/local/p2.png"));
    public static final LocalTaskPriority P3 = new LocalTaskPriority("P3", 0,
            Utilities.loadImage("org/netbeans/cubeon/local/p3.gif"));
    public static final LocalTaskPriority P4 = new LocalTaskPriority("P4", -1000,
            Utilities.loadImage("org/netbeans/cubeon/local/p4.png"));
    public static final LocalTaskPriority P5 = new LocalTaskPriority("P5", -10000,
            Utilities.loadImage("org/netbeans/cubeon/local/p5.png"));

    public LocalTaskPriorityProvider() {
        prioritys.add(P1);
        prioritys.add(P2);
        prioritys.add(P3);
        prioritys.add(P4);
        prioritys.add(P5);
    }

    public List<TaskPriority> getTaskPrioritys() {
        return new ArrayList<TaskPriority>(prioritys);
    }

    public TaskPriority getTaskPriorityById(String id) {
        for (TaskPriority tp : prioritys) {
            if (id.equals(tp.getId())) {
                return tp;
            }
        }
        // returning P3 if Priority not found
        return P3;
    }
}
