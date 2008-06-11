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
package org.netbeans.cubeon.tasks.spi;

import java.awt.Image;

/**
 *
 * @author Anuradha G
 */
public class TaskPriority {

    private final String id;
    private final int priority;
    private final Image image;

    public TaskPriority(String id, int priority, Image image) {
        this.id = id;
        this.priority = priority;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public String toString() {
        return id;
    }
}