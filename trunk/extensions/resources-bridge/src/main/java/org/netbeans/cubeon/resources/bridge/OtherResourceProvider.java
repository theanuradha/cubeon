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
package org.netbeans.cubeon.resources.bridge;

import java.io.File;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.cubeon.context.spi.TaskResourceSet;
import org.netbeans.cubeon.context.spi.TaskResouresProvider;
import org.netbeans.cubeon.tasks.spi.task.TaskElement;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha
 */
public class OtherResourceProvider implements TaskResouresProvider {

    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    public boolean isSupported(TaskElement taskElement) {
        return true;
    }

    public TaskResourceSet createResourceSet(TaskElement element) {
        return new OtherResourceSet(element, this);
    }
    
    static OtherResource toResource(DataObject dataObject) {
        
        if (dataObject != null) {
            FileObject primaryFile = dataObject.getPrimaryFile();
            Project owner = FileOwnerQuery.getOwner(primaryFile);
            if(owner == null){
                File toFile = FileUtil.toFile(primaryFile);
                if (toFile != null) {
                    String path = toFile.getAbsolutePath();
                    return new OtherResource(path);
                }
            }else{
                ProjectInformation pi = owner.getLookup().lookup(ProjectInformation.class);
                String path = FileUtil.getRelativePath(owner.getProjectDirectory(), primaryFile);
                return new OtherResource(pi.getName(), path);
            }
        }
        return null;
    }
}
