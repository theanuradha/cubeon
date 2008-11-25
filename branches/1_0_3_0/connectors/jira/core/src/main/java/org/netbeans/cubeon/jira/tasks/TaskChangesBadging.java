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
package org.netbeans.cubeon.jira.tasks;

import java.awt.Image;
import org.netbeans.cubeon.tasks.core.api.NodeUtils;
import org.netbeans.cubeon.tasks.spi.task.TaskBadgeProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public class TaskChangesBadging implements TaskBadgeProvider {

    public Image bageTaskIcon(TaskElement element, Image image) {
        JiraTask jiraTask = element.getLookup().lookup(JiraTask.class);
        if (jiraTask != null) {
            if (jiraTask.isModifiedFlag()) {
                //for now disable this
               // Image outgoingBadge = NodeUtils.outgoingBadge();
               // image = Utilities.mergeImages(image,outgoingBadge , -10, 0);
            }
        }
        return image;
    }
}
