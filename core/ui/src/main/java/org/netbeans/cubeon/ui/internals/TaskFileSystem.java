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

package org.netbeans.cubeon.ui.internals;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Anuradha G
 */
public class TaskFileSystem extends  FileSystem{

    @Override
    public String getDisplayName() {
        return "TASK_FILESYSTEM";
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public FileObject getRoot() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileObject findResource(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SystemAction[] getActions() {
       return new SystemAction[]{};
    }

}
