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
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataObject;

/**
 *
 * @author Anuradha
 */
public class TaskElementLoader extends DataLoader {

    public TaskElementLoader() {
        super(TaskElementDataObject.class.getName());
    }

    @Override
    protected DataObject handleFindDataObject(FileObject fileObject, RecognizedFiles arg1) throws IOException {
        return new TaskElementDataObject(fileObject, this);
    }
}
