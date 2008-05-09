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
import org.netbeans.cubeon.context.api.TaskFolderOparations;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
public class DefaultFolder implements TaskFolder, TaskFolderOparations {

    private String name;
    private String description;
    private FileObject fileObject;
    private TaskFolder parent;
    private final TaskFolderNode folderNode;

    DefaultFolder(DefaultFolder parent, String name,
            FileObject fileObject, String description) {
        this.parent = parent;

        this.name = name;
        this.fileObject = fileObject;
        this.description = description;

        folderNode = new TaskFolderNode(this);
    }

    public String getName() {
        return name;
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    public String getDescription() {
        return description;
    }

    public TaskFolder getParent() {
        return parent;
    }

    public boolean rename(String name) {
        try {
            //TODO : add vaidations
            fileObject.rename(fileObject.lock(), name, null);
            this.name = name;
            folderNode.setDisplayName(name);
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    public void setDescription(String description) {
        this.description = description;
        folderNode.setShortDescription(description);
    }

    public Lookup getLookup() {
        /**
         * lookup contain Node implementation and TaskFolderOparations
         */
        return Lookups.fixed(folderNode, this);
    }

    public TaskFolder addNewFolder(String name, String description) {
        try {

            FileObject fo = fileObject.createFolder(name);
            fo.setAttribute(DefaultFileSystem.DESCRIPTION_TAG, description);
            return new DefaultFolder(this, name, fo, description);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public boolean removeFolder(TaskFolder folder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TaskFolder> getSubFolders() {
        List<TaskFolder> folders = new ArrayList<TaskFolder>();
        FileObject[] fos = fileObject.getChildren();
        for (FileObject fo : fos) {
            if (fo.isFolder()) {
                String cname = fo.getName();
                String cdescription = (String) fo.getAttribute(DefaultFileSystem.DESCRIPTION_TAG);
                folders.add(new DefaultFolder(this, cname, fo, cdescription));
            }
        }
        return folders;
    }

    public boolean moveTo(TaskFolder parent) {
        //TODO : add vaidations
        FileObject parentPath = parent.getFileObject();
        try {
            fileObject = FileUtil.moveFile(fileObject, parentPath, name);
            this.parent = parent;
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);

        }

        return false;
    }

    public boolean copyTo(TaskFolder parent) {
        //TODO : add vaidations
        FileObject parentPath = parent.getFileObject();
        try {
            fileObject = FileUtil.copyFile(fileObject, parentPath, name);
            this.parent = parent;
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);

        }

        return false;
    }
}
