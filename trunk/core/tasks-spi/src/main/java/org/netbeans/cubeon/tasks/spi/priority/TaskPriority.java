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
package org.netbeans.cubeon.tasks.spi.priority;

import java.awt.Image;
import org.openide.util.Utilities;

/**
 *
 * @author Anuradha G
 */
public final class TaskPriority {

    private final PRIORITY id;
    private final Image image;
    private String text;

    /**
     * 
     */
    public enum PRIORITY {

        /**
         * 
         */
        P1,
        /**
         * 
         */
        P2,
        /**
         * 
         */
        P3,
        /**
         * 
         */
        P4,
        /**
         * 
         */
        P5
    }

    private TaskPriority(PRIORITY id, Image image, String text) {
        this.id = id;
        this.image = image;
        this.text = text;
    }

    /**
     * 
     * @param priority
     * @param text
     * @return
     */
    public static TaskPriority createPriority(PRIORITY priority, String text) {
        return new TaskPriority(priority, getImage(priority), text);
    }

    /**
     * 
     * @return
     */
    public PRIORITY getId() {
        return id;
    }

    /**
     * 
     * @return
     */
    public Image getImage() {
        return image;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TaskPriority other = (TaskPriority) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * 
     * @param priority
     * @return
     */
    public static Image getImage(PRIORITY priority) {

        switch (priority) {
            case P1:
                return Utilities.loadImage("org/netbeans/cubeon/tasks/spi/priority/p1.png");
            case P2:
                return Utilities.loadImage("org/netbeans/cubeon/tasks/spi/priority/p2.png");
            case P3:
                return Utilities.loadImage("org/netbeans/cubeon/tasks/spi/priority/p3.gif");
            case P4:
                return Utilities.loadImage("org/netbeans/cubeon/tasks/spi/priority/p4.png");
            case P5:
                return Utilities.loadImage("org/netbeans/cubeon/tasks/spi/priority/p5.png");

            default:
                return Utilities.loadImage("org/netbeans/cubeon/tasks/spi/priority/p3.gif");
        }

    }
}