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
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

/**
 *
 * @author Anuradha
 */
public class TaskElementDataObject extends DataObject implements SaveCookie {

    public TaskElementDataObject(FileObject fileObject, DataLoader dataLoader) throws DataObjectExistsException {
        super(fileObject, dataLoader);

    }

 
    public boolean isDeleteAllowed() {
        return false;
    }

    @Override
    public boolean isCopyAllowed() {
        return false;
    }

    @Override
    public boolean isMoveAllowed() {
        return false;
    }

    @Override
    public boolean isRenameAllowed() {
        return false;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected DataObject handleCopy(DataFolder arg0) throws IOException {
        return null;
    }

    @Override
    protected void handleDelete() throws IOException {
        //DONOTHING
    }

    @Override
    protected FileObject handleRename(String arg0) throws IOException {
        return null;
    }

    @Override
    protected FileObject handleMove(DataFolder arg0) throws IOException {
        return null;
    }

    @Override
    protected DataObject handleCreateFromTemplate(DataFolder arg0, String arg1) throws IOException {
        return null;
    }

    /**
     * Creates the node for this data object which is used for display purposes
     *
     * @return node
     */
    @Override
    protected Node createNodeDelegate() {
        return ((TaskElementFileObject) getPrimaryFile()).getNode();
    }

    public void save() throws IOException {
        SaveCookie cookie = ((TaskElementFileObject) getPrimaryFile()).getNode().getCookie(SaveCookie.class);
        cookie.save();
    }
}
