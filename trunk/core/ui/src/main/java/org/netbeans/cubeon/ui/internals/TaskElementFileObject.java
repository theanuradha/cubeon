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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.cubeon.tasks.spi.TaskElement;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;

/**
 *
 * @author Anuradha
 */
public class TaskElementFileObject extends FileObject {

    Map<String, Object> map = new HashMap<String, Object>();
    private TaskElement element;

    public TaskElementFileObject(TaskElement element) {
        this.element = element;
    }

    @Override
    public String getName() {
        return element.getName();
    }

    @Override
    public String getExt() {
        return "";
    }

    @Override
    public void rename(FileLock lock, String name, String ext) throws IOException {
        //DONOTHING
    }

    @Override
    public FileSystem getFileSystem() throws FileStateInvalidException {
        return new TaskFileSystem();
    }

    @Override
    public FileObject getParent() {
        //NO parent
        return null;
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public Date lastModified() {
        //TODO : add task modified Date
        return new Date();
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean isData() {
        return true;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void delete(FileLock lock) throws IOException {
        //DONOTHING
    }

    @Override
    public Object getAttribute(String attrName) {
        return map.get(attrName);
    }

    @Override
    public void setAttribute(String attrName, Object value) throws IOException {
        map.put(attrName, value);
    }

    @Override
    public Enumeration<String> getAttributes() {
        //TODO ADD 
        return null;
    }

    @Override
    public void addFileChangeListener(FileChangeListener fcl) {
        //DONOTHING
    }

    @Override
    public void removeFileChangeListener(FileChangeListener fcl) {
        //DONOTHING
    }

    @Override
    public long getSize() {
        return 0L;
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return null;
    }

    @Override
    public OutputStream getOutputStream(FileLock lock) throws IOException {
        return null;
    }

    @Override
    public FileLock lock() throws IOException {
        return null;
    }

    @Override
    public void setImportant(boolean b) {
        //DONOTHING
    }

    @Override
    public FileObject[] getChildren() {
        return new FileObject[0];
    }

    @Override
    public FileObject getFileObject(String name, String ext) {
        return null;
    }

    @Override
    public FileObject createFolder(String name) throws IOException {
        throw new IOException("Not supported");
    }

    @Override
    public FileObject createData(String name, String ext) throws IOException {
        throw new IOException("Not supported");
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
