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
package org.netbeans.cubeon.jira.repository;

import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.netbeans.cubeon.tasks.spi.task.TaskResolution;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.tasks.spi.repository.TaskResolutionProvider;

/**
 *
 * @author Anuradha G
 */
public class JiraTaskResolutionProvider implements TaskResolutionProvider {

    private List<TaskResolution> taskResolutiones = new ArrayList<TaskResolution>();

    public List<TaskResolution> getTaskResolutiones() {

        return new ArrayList<TaskResolution>(taskResolutiones);
    }

    public TaskResolution getTaskResolutionById(String id) {
        for (TaskResolution type : getTaskResolutiones()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        return null;
    }

    public void setTaskResolutions(List<TaskResolution> resolutionses) {
        this.taskResolutiones = new ArrayList<TaskResolution>(resolutionses);
    }

    public TaskResolution getTaskResolution(TaskElement element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTaskResolution(TaskElement element, TaskResolution resolution) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
