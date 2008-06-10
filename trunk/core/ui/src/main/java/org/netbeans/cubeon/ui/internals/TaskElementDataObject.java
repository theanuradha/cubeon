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

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.util.HelpCtx;

/**
 *
 * @author Anuradha
 */
public class TaskElementDataObject extends DataObject {

    private TaskElementFileObject fileObject;

    public TaskElementDataObject(TaskElementFileObject fileObject, DataLoader dataLoader) throws DataObjectExistsException {
        super(fileObject, dataLoader);
        this.fileObject = fileObject;
    }

    @Override
    public boolean isDeleteAllowed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCopyAllowed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMoveAllowed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRenameAllowed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HelpCtx getHelpCtx() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected DataObject handleCopy(DataFolder arg0) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void handleDelete() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected FileObject handleRename(String arg0) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected FileObject handleMove(DataFolder arg0) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected DataObject handleCreateFromTemplate(DataFolder arg0, String arg1) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
