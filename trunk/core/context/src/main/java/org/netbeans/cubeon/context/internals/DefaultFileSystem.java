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
import java.util.UUID;
import org.netbeans.cubeon.tasks.spi.TaskFolder;
import org.netbeans.cubeon.tasks.spi.TasksFileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Anuradha G
 */
public class DefaultFileSystem implements TasksFileSystem {

    private static final String BASE_PATH = "cubeon/tasks";
    private static final String UUID_TAG = "uuid";
    private static final String DESCRIPTION_TAG = "description";
    private FileObject root;

    public synchronized TaskFolder getRootFolder() {
        try {
            root = FileUtil.createFolder(root, BASE_PATH);

            String uuid = (String) root.getAttribute(UUID_TAG);
            String name = root.getName();
            String description = (String) root.getAttribute(DESCRIPTION_TAG);
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
                root.setAttribute(UUID_TAG, uuid);
            }
            if (description == null) {
                description = "";
                root.setAttribute(DESCRIPTION_TAG, description);
            }
            return new DefaultFolder(null, uuid, name, root, description);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert root != null;
        return null;
    }
}
