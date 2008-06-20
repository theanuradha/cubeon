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
package org.netbeans.cubeon.tasks.spi.task;

/**
 *
 * @author Anuradha G
 */
public class TaskStatus {

    private String id;
    private String text;

    /**
     * 
     * @param id
     * @param text
     */
    public TaskStatus(String id, String text) {
        this.id = id;
        this.text = text;
    }

    /**
     * 
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getText();
    }
}
