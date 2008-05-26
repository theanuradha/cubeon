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
package org.netbeans.cubeon.context.internals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.cubeon.context.api.TaskFolder;
import org.netbeans.cubeon.context.api.TasksFileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;

/**
 *
 * @author Anuradha G
 */
public class DefaultFileSystem implements TasksFileSystem {

    static final String BASE_PATH = "cubeon/tasks";
    static final String DEFAULT_PATH = "cubeon/uncategorized";
    static final String DESCRIPTION_TAG = "description";
    private final RootFolder rootfTaskFolder;

    public DefaultFileSystem() {

        FileObject rfileObject = null;

        try {
            rfileObject = FileUtil.createFolder(Repository.getDefault().
                    getDefaultFileSystem().getRoot(), BASE_PATH);


        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert rfileObject != null;

        String name = rfileObject.getName();

        rootfTaskFolder = new RootFolder(null, name, rfileObject, null);


    }

    public TaskFolder getDefaultFolder() {

        return rootfTaskFolder.getDefaultFolder();
    }

    public List<TaskFolder> getFolders() {

        return new ArrayList<TaskFolder>(rootfTaskFolder.getSubFolders());
    }

    public TaskFolder getRootTaskFolder() {
        return rootfTaskFolder;
    }
}
