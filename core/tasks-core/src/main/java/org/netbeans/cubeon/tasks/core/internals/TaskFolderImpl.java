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
package org.netbeans.cubeon.tasks.core.internals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.cubeon.tasks.core.api.RefreshableChildren;
import org.netbeans.cubeon.tasks.core.api.TaskFolder;
import org.netbeans.cubeon.tasks.core.api.TaskFolderOparations;
import org.netbeans.cubeon.tasks.core.api.TaskFolderRefreshable;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Anuradha G
 */
class TaskFolderImpl implements TaskFolder, TaskFolderOparations, TaskFolderRefreshable {

    private String name;
    private String description;
    protected FileObject fileObject;
    protected TaskFolder parent;
    protected Node folderNode;
    protected RefreshableChildren folderChildren;
    protected final List<TaskFolderImpl> taskFolders = new ArrayList<TaskFolderImpl>();
    protected final List<TaskElement> taskElements = new ArrayList<TaskElement>();
    protected final PersistenceHandler persistenceHandler;

    protected TaskFolderImpl(TaskFolderImpl parent, String name,
            FileObject fileObject, String description, boolean basic) {
        this.parent = parent;
        this.name = name;
        this.fileObject = fileObject;
        this.description = description;

        persistenceHandler = new PersistenceHandler(this);

        if (!basic) {
            refreshFolders();
            persistenceHandler.refresh();
            folderChildren = new TaskElementChilren(this);
            folderNode = new TaskFolderNode(this, folderChildren.getChildren());
        }
    }

    TaskFolderImpl(TaskFolderImpl parent, String name,
            FileObject fileObject, String description) {
        this(parent, name, fileObject, description, false);

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
            //get file lock
            FileLock lock = fileObject.lock();
            fileObject.rename(lock, name, null);
            //release lock after rename 
            lock.releaseLock();
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
         * lookup contain Node implementation , TaskFolderOparations,RefreshProvider
         */
        return Lookups.fixed(folderNode, this);
    }

    public TaskFolder addNewFolder(String name, String description) {
        try {

            FileObject fo = fileObject.createFolder(name);
            fo.setAttribute(DefaultFileSystem.DESCRIPTION_TAG, description);
            TaskFolderImpl impl = new TaskFolderImpl(this, name, fo, description);
            taskFolders.add(impl);
            return impl;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public boolean removeFolder(TaskFolder folder) {
        try {
            TaskFolderImpl folderImpl = folder.getLookup().lookup(TaskFolderImpl.class);
            folderImpl.getFileObject().delete();
            taskFolders.remove(folderImpl);
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    protected void refreshFolders() {
        clearFolder(this);

        FileObject[] fos = fileObject.getChildren();
        for (FileObject fo : fos) {
            if (fo.isFolder()) {
                String cname = fo.getName();
                String cdescription = (String) fo.getAttribute(DefaultFileSystem.DESCRIPTION_TAG);
                taskFolders.add(new TaskFolderImpl(this, cname, fo, cdescription));
            }
        }

    }

    public List<TaskFolder> getSubFolders() {
        Collections.sort(taskFolders, new TaskFolderComparator());
        return new ArrayList<TaskFolder>(taskFolders);
    }

    public boolean moveTo(TaskFolder parent) {
        //TODO : add vaidations
        TaskFolderImpl folderImpl = parent.getLookup().lookup(TaskFolderImpl.class);
        FileObject parentPath = folderImpl.getFileObject();
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
        TaskFolderImpl folderImpl = parent.getLookup().lookup(TaskFolderImpl.class);
        FileObject parentPath = folderImpl.getFileObject();
        try {
            fileObject = FileUtil.copyFile(fileObject, parentPath, name);
            this.parent = parent;
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);

        }

        return false;
    }

    public TaskElement addTaskElement(TaskElement element) {
        persistenceHandler.addTaskElement(element);
        taskElements.add(element);
        return element;
    }

    public boolean removeTaskElement(TaskElement element) {
        persistenceHandler.removeTaskElement(element);
        taskElements.remove(element);
        return true;
    }

    public List<TaskElement> getTaskElements() {
        return new ArrayList<TaskElement>(taskElements);
    }

    boolean setTaskElements(Collection<? extends TaskElement> c) {

        taskElements.clear();
        return taskElements.addAll(c);
    }

    public void refreshContent() {
        synchronized (this) {
            refreshFolders();
            synchronize();
        }
    }

    protected void clearFolder(TaskFolderImpl impl) {
        for (TaskFolderImpl taskFolder : impl.taskFolders) {
            clearFolder(taskFolder);
        }

        impl.taskFolders.clear();
    }

    public void synchronize() {
        persistenceHandler.refresh();
        refeshNode();
    }

    public void refeshNode() {
        folderChildren.refreshContent();
    }

    public boolean contains(TaskElement element) {
        return taskElements.contains(element);
    }


}
